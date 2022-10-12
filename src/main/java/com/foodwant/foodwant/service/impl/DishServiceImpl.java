package com.foodwant.foodwant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.foodwant.foodwant.common.CustomException;
import com.foodwant.foodwant.dto.DishDto;
import com.foodwant.foodwant.entity.Dish;
import com.foodwant.foodwant.entity.DishFlavor;
import com.foodwant.foodwant.mapper.DishMapper;
import com.foodwant.foodwant.service.DishFlavorService;
import com.foodwant.foodwant.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-09 上午 01:41
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品，同時插入菜品對應的口味數據
     *
     * @param dishDto
     */
    @Override
    public void saveWithFavor(DishDto dishDto) {
        //保存菜品基本資訊到dish表
        this.save(dishDto);

        //再存入DB後取出dish的ID，用來等等在dish_flavor表中來儲為哪一菜的口味
        //dish資訊插入表後，其自增ID為由雪花算法算出，在映射回實體類中同名的屬性
        Long dishId = dishDto.getId();

        //traverse flavors list，將其中每隔DishFlavor類中的dishId復值剛剛拿到的菜品的ID
        List<DishFlavor> flavors = dishDto.getFlavors();
//        for(DishFlavor dishFlavor : flavors){
//            dishFlavor.setDishId(dishId);
//        }
        //Lambda           參數          方法=要執行的功能
        //flavors.forEach( (dishFlavor) -> dishFlavor.setId(dishId));

        flavors = flavors.stream().map( (item) ->{ //item為List中查出來的DishFlavor
            item.setDishId(dishId); //對每一個DishFlavor加工
            return item;            //加工後要返回
        }).collect(Collectors.toList()); //重新組成list

        //保存菜品口味數據到dish_flavor表
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //先查菜品基本資訊
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //再查口味，條件為where dish_id = id
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);

        return dishDto;
    }

    @Override
    public void updateWithFavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //對於口味->先刪再新增

        //清理口味---dish_flavor的delete
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //從dishDto抽出口味集合，並且再口味即河中的每個口味設置菜品Id
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根據id修改對應菜品的狀態為參數status
     * @param id
     * @param status
     */
    @Override
    public void updatestatus(Long[] id, int status) {
        LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Dish::getId,id).set(Dish::getStatus,status);

        this.update(null,lambdaUpdateWrapper);
    }

    /**
     * 根據id刪除菜色，只有停售才能刪除
     * @param asList
     */
    @Override
    public void removeWithStatus(List<Long> asList) {
        //只有停售才能刪除
        //先查啟售狀態的有幾個
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.in(Dish::getId,asList);
        int count = this.count(queryWrapper);

        if(count > 0){//>0表示有啟售狀態的菜色 不能刪除 拋出例外
            throw new CustomException("請先停售菜色在進行刪除");
        }

        //刪除菜色
        this.removeByIds(asList);


    }
}
