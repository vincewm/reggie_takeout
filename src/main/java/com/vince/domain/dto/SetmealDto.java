package com.vince.domain.dto;

import com.vince.domain.Setmeal;
import com.vince.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    //套餐和菜品关系的list
    private List<SetmealDish> setmealDishes;
    //所属分类名
    private String categoryName;
}

