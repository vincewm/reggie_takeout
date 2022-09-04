package com.vince.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vince.common.BaseContext;
import com.vince.common.CustomException;
import com.vince.dao.OrderDao;
import com.vince.dao.UserDao;
import com.vince.domain.AddressBook;
import com.vince.domain.Order;
import com.vince.domain.OrderDetail;
import com.vince.domain.ShoppingCart;
import com.vince.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements OrderService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public boolean submit(Order order) {
        long orderId = IdWorker.getId();
        order.setNumber(String.valueOf(orderId));   //只有订单表的number是订单号
        //获取当前用户的购物车，遍历赋值给订单详情并计算购物车金额
        Long userId = BaseContext.getThreadId();
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        AtomicInteger amount=new AtomicInteger(0);
        //保存多条订单详情，来源于购物车list的加工
        List<OrderDetail> orderDetails=shoppingCarts.stream().map(item->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        log.info("订单那详情："+orderDetails.toString());
        if(!orderDetailService.saveBatch(orderDetails)) throw new CustomException("订单详情保存失败");

        //保存订单
        order.setStatus(2);//待派送
        order.setUserId(userId);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setUserName(userService.getById(userId).getName());
        order.setAmount(new BigDecimal(amount.get()));
        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());
        order.setPhone(addressBook.getPhone());
        order.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        order.setConsignee(addressBook.getConsignee());
        log.info("订单：{}",order);
        if(!this.save(order)) throw new CustomException("订单保存失败");
//        shoppingCartService.remove();
        return true;
    }
}
