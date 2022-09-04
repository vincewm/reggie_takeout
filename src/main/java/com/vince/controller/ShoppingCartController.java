package com.vince.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vince.common.BaseContext;
import com.vince.common.R;
import com.vince.domain.ShoppingCart;
import com.vince.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @RequestMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getThreadId();
        shoppingCart.setUserId(userId);
        //查询这个用户的购物车是否已经有此东西
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        wrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        wrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart existShoppingCart = shoppingCartService.getOne(wrapper);
        if(existShoppingCart!=null){
            //这个菜品在购物车已经有了，number+1
            existShoppingCart.setNumber(existShoppingCart.getNumber()+1);
            shoppingCartService.updateById(existShoppingCart);
            return R.success(existShoppingCart);
        }else {
            //这个菜品在购物车没有,number默认值是1
            shoppingCartService.save(shoppingCart);
            return R.success(shoppingCart);
        }
    }
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getThreadId());
        wrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        if(shoppingCarts.size()>0) return R.success(shoppingCarts);
        else return R.error("购物车空空如也");
    }

    /**
     * 减少数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(dishId!=null,ShoppingCart::getDishId,dishId);
        wrapper.eq(setmealId!=null,ShoppingCart::getSetmealId,setmealId);
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getThreadId());
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if(one.getNumber()>1){
            //数量大于1，减1
            one.setNumber(one.getNumber()-1);
            shoppingCartService.updateById(one);
            return R.success("减少成功");
        }else{
            //数量为1,，在减少后为0，删除商品
            shoppingCartService.removeById(one);
            return R.success("这个商品没了");
        }
    }
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getThreadId());
        if(shoppingCartService.remove(wrapper)) return R.success("清空成功");
        else return R.error("清空失败");
    }
}
