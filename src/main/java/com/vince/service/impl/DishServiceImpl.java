package com.vince.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vince.common.CustomException;
import com.vince.dao.DishDao;
import com.vince.domain.Category;
import com.vince.domain.Dish;
import com.vince.domain.DishFlavor;
import com.vince.domain.dto.DishDto;
import com.vince.service.CategoryService;
import com.vince.service.DishFlavorService;
import com.vince.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    @Transactional
    public boolean saveWithFlavor(DishDto dishDto) {
        if(!this.save(dishDto)) return false;
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map(item->{item.setDishId(dishDto.getId());return item;}).collect(Collectors.toList());
        if(!dishFlavorService.saveBatch(flavors)) return false;
        return true;
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //给dishDto添加味道
        LambdaQueryWrapper<DishFlavor> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(wrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public boolean updateWithFlavor(DishDto dishDto) {
//        Dish dish = new Dish();
//        BeanUtils.copyProperties(dishDto,dish);
        //直接传dto也能查
        if(!this.updateById(dishDto)) return false;
        LambdaQueryWrapper<DishFlavor> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        if(!dishFlavorService.remove(wrapper)) return false;
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map(item->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        if(!dishFlavorService.saveBatch(flavors)) return false;
        return true;
    }

}
