package com.vince.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vince.dao.DishFlavorDao;
import com.vince.domain.DishFlavor;
import com.vince.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorDao, DishFlavor> implements DishFlavorService {
}
