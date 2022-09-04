package com.vince.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vince.domain.Employee;
import com.vince.domain.Setmeal;
import com.vince.domain.dto.SetmealDto;

public interface SetmealService extends IService<Setmeal> {
    public boolean saveWithDish(SetmealDto setmealDto);
}
