package com.foodwant.foodwant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.foodwant.foodwant.entity.Category;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-08 下午 05:08
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
