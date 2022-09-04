package com.vince.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vince.domain.Dish;
import com.vince.domain.dto.DishDto;

public interface DishService extends IService<Dish> {
    public boolean saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long id);
    public boolean updateWithFlavor(DishDto dishDto);
}
