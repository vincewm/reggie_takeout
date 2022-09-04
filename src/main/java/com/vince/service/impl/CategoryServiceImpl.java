package com.vince.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vince.common.CustomException;
import com.vince.dao.CategoryDao;
import com.vince.domain.Category;
import com.vince.domain.Dish;
import com.vince.domain.Setmeal;
import com.vince.service.CategoryService;
import com.vince.service.DishService;
import com.vince.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryDao categoryDao;
    @Override
    public boolean remove(Long id){
        LambdaQueryWrapper<Setmeal> wrapper1=new LambdaQueryWrapper<>();
        wrapper1.eq(Setmeal::getCategoryId,id);
        LambdaQueryWrapper<Dish> wrapper2=new LambdaQueryWrapper<>();
        wrapper2.eq(Dish::getCategoryId,id);
        log.info("套餐查询：{}",setmealService.getMap(wrapper1));log.info("菜品查询：{}",dishService.getMap(wrapper2));
        //查到有菜品或套餐使用这个分类
        if(setmealService.count(wrapper1)>0) throw new CustomException("有套餐正在使用这个分类");
        if(dishService.count(wrapper2)>0) throw new CustomException("有菜品正在使用这个分类");
        if (categoryDao.deleteById(id)>0) return true;
        else throw new CustomException("未知错误删除失败，不是有菜品或套餐正在使用这个分类的原因");
    }
}
