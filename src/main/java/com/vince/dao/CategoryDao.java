package com.vince.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vince.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface CategoryDao extends BaseMapper<Category> {
}
