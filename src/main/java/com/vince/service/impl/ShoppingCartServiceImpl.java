package com.vince.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vince.dao.ShoppingCartDao;
import com.vince.domain.ShoppingCart;
import com.vince.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao,ShoppingCart>implements ShoppingCartService {
}
