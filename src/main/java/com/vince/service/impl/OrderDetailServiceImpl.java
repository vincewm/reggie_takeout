package com.vince.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vince.dao.OrderDetailDao;
import com.vince.domain.OrderDetail;
import com.vince.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {
}
