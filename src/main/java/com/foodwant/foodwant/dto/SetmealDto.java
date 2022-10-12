package com.foodwant.foodwant.dto;

import com.foodwant.foodwant.entity.Setmeal;
import com.foodwant.foodwant.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
