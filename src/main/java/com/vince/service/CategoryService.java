package com.vince.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vince.domain.Category;

public interface CategoryService extends IService<Category> {
    boolean remove(Long id);
}
