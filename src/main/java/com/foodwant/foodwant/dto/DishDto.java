package com.foodwant.foodwant.dto;

import com.foodwant.foodwant.entity.Dish;
import com.foodwant.foodwant.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();//瀏覽器傳的flovors list中的name和value會傳進來DishFlavor類

    private String categoryName;

    private Integer copies;
}
