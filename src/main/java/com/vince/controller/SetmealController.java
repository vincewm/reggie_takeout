package com.vince.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vince.common.R;
import com.vince.domain.Category;
import com.vince.domain.Setmeal;
import com.vince.domain.dto.SetmealDto;
import com.vince.service.CategoryService;
import com.vince.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        if (setmealService.saveWithDish(setmealDto)) return R.success("保存成功");
        else return R.error("保存失败");
    }

    /**
     * 修改套餐回显。
     * 示例http://localhost/setmeal/1565946733836435458
     * @param id
     * @return
     */
//    @GetMapping("/{id}")
//    public R<Setmeal> update(@PathVariable Long id){
//        Setmeal setmeal = setmealService.getById(id);
//        return R.success(setmeal);
//    }
    /**
     * 菜品分页展示
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") Integer currentPage, Integer pageSize, String name) {
        Page<Setmeal> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StringUtils.isEmpty(name), Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(page, wrapper);
        Page<SetmealDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage, "records");
        List<Setmeal> setmeals = page.getRecords();
        dtoPage.setRecords(setmeals.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            LambdaQueryWrapper<Category> wrapper1 = new LambdaQueryWrapper<>();
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList()));
        log.info("dtoPage:{},{},{}", dtoPage.getTotal(), dtoPage.getPages(), dtoPage.getRecords());
        if (pageSize > 0) return R.success(dtoPage);
        else return R.error("没有数据");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info(ids.toString());
        if (setmealService.removeBatchByIds(ids)) return R.success("删除成功");
        else
            return R.error("删除失败");
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "'setmael_'+#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        wrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        wrapper.eq(setmeal.getName()!=null,Setmeal::getName,setmeal.getName());
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(wrapper);
        if(setmeals.size()>0) return R.success(setmeals);
        else return R.error("没有数据");
    }
}
