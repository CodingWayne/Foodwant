package com.foodwant.foodwant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.foodwant.foodwant.dto.DishDto;
import com.foodwant.foodwant.entity.Dish;

import java.util.List;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-09 上午 01:40
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同時插入菜品對應的口味數據
    //DB: dish, dish_flavor
    public void saveWithFavor(DishDto dishDto);

    //根據Id查詢菜品和口味資訊
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品資訊同時更新口味資訊
    public void updateWithFavor(DishDto dishDto);

    //根據菜品id變更菜品狀態
    void updatestatus(Long[] id, int status);

    void removeWithStatus(List<Long> asList);

}
