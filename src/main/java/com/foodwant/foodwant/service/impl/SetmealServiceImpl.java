package com.foodwant.foodwant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.foodwant.foodwant.common.CustomException;
import com.foodwant.foodwant.dto.DishDto;
import com.foodwant.foodwant.dto.SetmealDto;
import com.foodwant.foodwant.entity.*;
import com.foodwant.foodwant.mapper.SetmealMapper;
import com.foodwant.foodwant.service.CategoryService;
import com.foodwant.foodwant.service.SetmealDishService;
import com.foodwant.foodwant.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-09 上午 01:42
 */

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    @Lazy(true)
    private CategoryService categoryService;

    /**
     * 新增套餐，同時新增套餐和菜品的關係
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //1. setmeal表中insert套餐資訊
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //2.swtmeal_dish表中insert與該套餐下的餐品
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public Page querylist(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);

        //有name的話要查
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.like(name != null,Setmeal::getName,name);

        //依時間排序
        setmealWrapper.orderByDesc(Setmeal::getUpdateTime);

        this.page(setmealPage,setmealWrapper);

        //此時setmealWrapper裡的records裡沒有頁面要的categoryName的資訊
        // categoryNam要根據category id去category表查詢

        Page<SetmealDto> setmealDtoPage = new Page<>();
        //先賦值給setmealDtoPage，但是不需要處理records，等等在封裝SetmealDto進去
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        //從setmealPage中取出records
        List<Setmeal> setmealPageRecords = setmealPage.getRecords();
        //建立一個List<SetmealDto>，並修改裡面每一個SetmealDto

        List<SetmealDto> setmealDtoList = setmealPageRecords.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //賦值給setmealdto
            BeanUtils.copyProperties(item,setmealDto);
            //根據setmeal中的categoryid 去category表查對應的name，再賦值
            Category category = categoryService.getById(item.getCategoryId());
            if(category != null){

                setmealDto.setCategoryName(category.getName());
            }

            return setmealDto;
        }).collect(Collectors.toList());


        setmealDtoPage.setRecords(setmealDtoList);
        return setmealDtoPage;


    }

    @Override
    public void updatestatus(Long[] id, int status) {
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Setmeal::getId,id).set(Setmeal::getStatus,status);

        this.update(null,lambdaUpdateWrapper);
    }

    @Override
    public void removeWithDisg(List<Long> id) {
        //只有停售狀態才能刪除
        //先查表看狀態=1 不能刪除的個數是否>0
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Setmeal::getStatus,1);
        queryWrapper.in(Setmeal::getId,id);
        int count = this.count(queryWrapper);

        if(count > 0){
            throw new CustomException("請先停售套餐再刪除套餐");
        }

        this.removeByIds(id);

        //再來刪除setmeal_dish表中有相同setmealid的餐品
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,id);

        setmealDishService.remove(lambdaQueryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //先查套餐基本資訊
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //再查菜品，條件為where setmeal_id = id
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());

        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新dish表基本信息
        this.updateById(setmealDto);

        //對於菜品->先刪再新增

        //清理菜品---dish_flavor的delete
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        //從setmealDto抽出套餐菜品集合，並重新加上套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
