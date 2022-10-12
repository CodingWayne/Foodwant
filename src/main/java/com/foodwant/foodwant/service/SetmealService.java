package com.foodwant.foodwant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.foodwant.foodwant.dto.SetmealDto;
import com.foodwant.foodwant.entity.Setmeal;

import java.util.List;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-09 上午 01:40
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同時新增套餐和菜品的關係
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 套餐分頁查詢
     * @param page
     * @param pageSize
     * @param name
     */
    public Page querylist(int page, int pageSize, String name);

    void updatestatus(Long[] id, int status);

    void removeWithDisg(List<Long> id);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}

