package com.vince.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vince.common.R;
import com.vince.domain.Category;
import com.vince.domain.Dish;
import com.vince.domain.DishFlavor;
import com.vince.domain.dto.DishDto;
import com.vince.service.CategoryService;
import com.vince.service.DishFlavorService;
import com.vince.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("新增菜品传来的dish：{}",dishDto);
        if(dishService.saveWithFlavor(dishDto)) return R.success("保存成功");
        else return R.error("保存失败");
    }


    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }
    /**
     * 修改菜品回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getUpdate(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        log.info("dishDto:{}",dishDto);
        return R.success(dishDto);
    }

    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        if(dishService.updateWithFlavor(dishDto)) return R.success("更新成功");
        else return R.error("更新失败");
    }

    /**
     * 条件查询list，只查询开启售卖的。
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();
        wrapper.like(!StringUtils.isEmpty(dish.getName()),Dish::getName,dish.getName());
        wrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //只查起售的
        wrapper.eq(Dish::getStatus,1);
        wrapper.orderByDesc(Dish::getUpdateTime);
        //查到了条件下所有菜品的基本信息
        List<Dish> dishes = dishService.list(wrapper);
        //所有菜品基本信息加工成包括分类名和味道
        List<DishDto> dishDtos=dishes.stream().map(item->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //设置菜品所属分类名
            Category category = categoryService.getById(dish.getCategoryId());
            if(category!=null) dishDto.setCategoryName(category.getName());
            //设置这个菜品对应的所有味道
            LambdaQueryWrapper<DishFlavor> wrapper1=new LambdaQueryWrapper<>();
            wrapper1.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(wrapper1);
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtos);
    }
}
