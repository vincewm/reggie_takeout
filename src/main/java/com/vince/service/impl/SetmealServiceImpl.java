package com.vince.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vince.dao.EmployeeDao;
import com.vince.dao.SetmealDao;
import com.vince.domain.Employee;
import com.vince.domain.Setmeal;
import com.vince.domain.SetmealDish;
import com.vince.domain.dto.SetmealDto;
import com.vince.service.EmployeeService;
import com.vince.service.SetmealDishService;
import com.vince.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    @Transactional
    public boolean saveWithDish(SetmealDto setmealDto) {
        //保存后setmealDao就生成了套餐id
        if(!this.save(setmealDto))return false;
        //获取套菜关系的list
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //加工list，每个套餐关系设置前面生成的套餐id，其他参数都在表单传来了，或有默认值
        setmealDishes.stream().map(item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        if(!setmealDishService.saveBatch(setmealDishes)) return false;
        return true;
    }
}
