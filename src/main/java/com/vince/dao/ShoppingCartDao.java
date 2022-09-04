package com.vince.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vince.domain.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartDao extends BaseMapper<ShoppingCart> {
}
