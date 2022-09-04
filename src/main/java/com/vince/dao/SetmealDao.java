package com.vince.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vince.domain.Employee;
import com.vince.domain.Setmeal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealDao extends BaseMapper<Setmeal> {
}
