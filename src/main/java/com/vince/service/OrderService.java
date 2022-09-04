package com.vince.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vince.domain.Order;

public interface OrderService extends IService<Order> {
    boolean submit(Order order);
}
