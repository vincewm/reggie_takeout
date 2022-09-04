package com.vince.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vince.common.BaseContext;
import com.vince.common.R;
import com.vince.domain.Order;
import com.vince.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     * @param order
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Order order){
        if(orderService.submit(order)) return R.success("下单成功");
        return R.error("下单失败");
    }

    /**
     * 分页查询账单
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(@RequestParam("page") Integer currentPage,Integer pageSize){
        Page<Order> page=new Page<>(currentPage,pageSize);
        LambdaQueryWrapper<Order> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId,BaseContext.getThreadId());
        wrapper.orderByDesc(Order::getCheckoutTime);
        orderService.page(page,wrapper);
        if(pageSize>0) return R.success(page);
        else return R.error("没有数据");
    }
    /**
     * 分页查询账单
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") Integer currentPage,Integer pageSize){
        Page<Order> page=new Page<>(currentPage,pageSize);
        LambdaQueryWrapper<Order> wrapper=new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCheckoutTime);
        orderService.page(page,wrapper);
        page.setRecords(page.getRecords().stream().map(item->{
            if(item.getUserName()==null) item.setUserName("没有设置用户名");
            return item;
        }).collect(Collectors.toList()));
        if(pageSize>0) return R.success(page);
        else return R.error("没有数据");
    }
}
