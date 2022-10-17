package com.foodwant.foodwant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.foodwant.foodwant.common.BaseContext;
import com.foodwant.foodwant.common.CustomException;
import com.foodwant.foodwant.common.R;
import com.foodwant.foodwant.common.UserBaseContext;
import com.foodwant.foodwant.entity.ShoppingCart;
import com.foodwant.foodwant.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-16 下午 08:00
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(HttpServletRequest request, @RequestBody ShoppingCart shoppingCart){
        log.info("購物車數據:{}",shoppingCart);

        //設置用戶id，指定當前是哪個用戶的購物車數據
        Long currentId = UserBaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //添加到購物車的是菜色
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //添加到購物車的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查詢當前菜色或者套餐是否在購物車中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if(cartServiceOne != null){
            //如果已經存在，就在原來數量基礎上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else{
            //如果不存在，則添加到購物車，數量默認就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);



    }
    /**
     * 查看購物車
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看購物車...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, UserBaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }
    /**
     * 清空購物車
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //SQL:delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,UserBaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("清空購物車成功");
    }

    /**
     * 購物車內容-1
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        //設置用戶id，指定當前是哪個用戶的購物車數據
        Long currentId = UserBaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //要改的是菜色
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //要改的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        Integer number = cartServiceOne.getNumber();
        cartServiceOne.setNumber(number-1);
        if(cartServiceOne.getNumber() == 0){
            shoppingCartService.removeById(cartServiceOne);
        }
        shoppingCartService.updateById(cartServiceOne);
        return R.success("數量減少成功");

    }

}
