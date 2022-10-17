package com.foodwant.foodwant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodwant.foodwant.common.R;
import com.foodwant.foodwant.dto.DishDto;
import com.foodwant.foodwant.entity.Category;
import com.foodwant.foodwant.entity.Dish;
import com.foodwant.foodwant.entity.DishFlavor;
import com.foodwant.foodwant.service.CategoryService;
import com.foodwant.foodwant.service.DishFlavorService;
import com.foodwant.foodwant.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-09 下午 04:12
 */
@RestController
@RequestMapping("dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        //傳入的數據除了dish外還有口味集合，無法單純封裝
        //解法: 導入DishDTO，繼承於Dish，等於多套一層來封裝
        log.info(dishDto.toString());
        dishService.saveWithFavor(dishDto);
        //雙表處裡
        return R.success("新增菜品成功");

    }

    /**
     * 菜品分頁查詢
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //條件
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //有傳入name的話就查name
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        //根據 getUpdateTime排
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, lambdaQueryWrapper);
        //此時pageInfo中每個dish的categoryid必須要轉換成對應的風味名，因此要去category表根據
        //categoryid查出對應的風味然猴得到風味名，
        //但是page<Dish>裡沒有位置放查出來的風味名，
        //這時就咬使用DishDto，內有categoryName來存放風味名，且DishDto也繼承了Dish，可以使用Dish的屬性


        //新new的Page<DishDto>裡尚未賦值，因此物件複製，將已經查好的Page<Dish>複製過來
        //page中的records集合為裝查詢結果的list，不需要複製，要對其進行修改
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        //取出records
        List<Dish> records = pageInfo.getRecords();
        //將Dish類型的list處理後 存到DishDto類型的list
        List<DishDto> list = records.stream().map((item) -> {
            //先建立DishDto物件，修改後要放到list裡
            DishDto dishDto = new DishDto();
            //將Dish屬性複製給DishDto，此時只剩DishDto的categoryName要處理
            BeanUtils.copyProperties(item, dishDto);
            //從Dish中取出風味的id
            Long categoryId = item.getCategoryId();
            //根據風味id查詢對應的風類
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                //從風味物件中取出風味名並賦值給此時只剩DishDto的categoryName要處理
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根據ID查詢菜品和對應口味資訊
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        //傳入的數據除了dish外還有口味集合，無法單純封裝
        //解法: 導入DishDTO，繼承於Dish，等於多套一層來封裝
        log.info(dishDto.toString());

        dishService.updateWithFavor(dishDto);
        //雙表處裡
        return R.success("修改菜品成功");

    }

    /**
     * 根據id刪除菜品
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] id) {
        for (Long e : id) {
            log.info("刪除菜品:id= {}", e);
        }

        dishService.removeWithStatus(Arrays.asList(id));
        return R.success("刪除成功");
    }

    /**
     * 根據id更改該菜品狀態為status
     *
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(Long[] id, @PathVariable("status") int status) {
        log.info("更新菜品狀態:id= {} 狀態: {}", id, status);

        dishService.updatestatus(id, status);

        return R.success("菜品狀態修改成功");

    }

    /**
     * 查詢菜色的數據
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        //條件 只需查販售中 status=1之菜品
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId()).eq(Dish::getStatus, 1);
        lambdaQueryWrapper.eq(dish.getName() != null, Dish::getName, dish.getName());
        //排序
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        //查詢不同菜色對應的口味，使用DisgDto來放口味，遍歷取出list<Dsih>，
        // 裝入口味後放到list<Dishdto>
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//菜色的分類的Id
            //根据id查询菜色分類
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //當前菜色id，根據id查詢對應口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);

    }

}
