package com.foodwant.foodwant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodwant.foodwant.common.R;
import com.foodwant.foodwant.dto.DishDto;
import com.foodwant.foodwant.dto.SetmealDto;
import com.foodwant.foodwant.entity.Setmeal;
import com.foodwant.foodwant.service.SetmealDishService;
import com.foodwant.foodwant.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 套餐管理
 * @author JIA-YANG, LAI
 * @create 2022-10-10 下午 03:40
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {


    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐: {}",setmealDto.toString());
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){

        Page<SetmealDto> setmealDtoPage = setmealService.querylist(page, pageSize, name);

        return R.success(setmealDtoPage) ;

    }

    /**
     * 根據id刪除套餐
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("id") List<Long> id){

        setmealService.removeWithDisg(id);
        return R.success("刪除成功");
    }

    /**
     * 根據id更改該菜品狀態為status
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(Long[] id,@PathVariable("status") int status){
        log.info("更新套餐狀態:id= {} 狀態: {}",id,status);

        setmealService.updatestatus(id,status);

        return  R.success("套餐狀態修改成功");

    }

    /**
     * 根據ID查詢套餐和對應口味資訊
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long  id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        //傳入的數據除了dish外還有口味集合，無法單純封裝
        //解法: 導入DishDTO，繼承於Dish，等於多套一層來封裝
        log.info(setmealDto.toString());

        setmealService.updateWithDish(setmealDto);
        //雙表處裡
        return R.success("修改菜品成功");

    }

    /**
     * 查詢套餐數據
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }






}
