package com.foodwant.foodwant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodwant.foodwant.common.R;
import com.foodwant.foodwant.entity.Category;
import com.foodwant.foodwant.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分類管理
 * @author JIA-YANG, LAI
 * @create 2022-10-08 下午 05:10
 */

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分類
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("save category: {}",category);
        categoryService.save(category);
        return R.success("保存成功");
    }

    /**
     * 分頁查詢
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //條件 以sort排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);

        //進行分頁查詢
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根據id刪除分類
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id ){
        log.info("刪除分類, ID為: {}",id);

        //categoryService.removeById(id);
        categoryService.remove(id);
        return R.success("分類刪除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("分類修改: {}",category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根據條件查分類數據
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list( Category category){
        //條件
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //先判斷type有無
        lambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //先根據sort排序再根據createupdatetime
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);

    }


}
