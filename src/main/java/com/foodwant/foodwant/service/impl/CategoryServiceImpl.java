package com.foodwant.foodwant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.foodwant.foodwant.common.CustomException;
import com.foodwant.foodwant.entity.Category;
import com.foodwant.foodwant.entity.Dish;
import com.foodwant.foodwant.entity.Setmeal;
import com.foodwant.foodwant.mapper.CategoryMapper;
import com.foodwant.foodwant.service.CategoryService;
import com.foodwant.foodwant.service.DishService;
import com.foodwant.foodwant.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-08 下午 05:08
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根據id刪除分類，刪除前要先判斷是否關聯
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查詢要刪的分類是否關聯菜品，是的話拋出異常
        //使用dishService去使用dishMapper查dish表
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if(dishCount > 0){
            //已關聯菜品，拋出異常
            throw new CustomException("請先刪除分類下的菜色");
        }

        //查詢要刪的分類是否關聯套餐，是的話拋出異常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if(setmealCount > 0){
            //已關聯套餐，拋出異常
            throw new CustomException("請先刪除分類下的菜色");
        }
        //正常刪除
        super.removeById(id);
    }
}
