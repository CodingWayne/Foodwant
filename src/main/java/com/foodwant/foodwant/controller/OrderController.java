package com.foodwant.foodwant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodwant.foodwant.common.BaseContext;
import com.foodwant.foodwant.common.R;
import com.foodwant.foodwant.dto.OrdersDto;
import com.foodwant.foodwant.entity.OrderDetail;
import com.foodwant.foodwant.entity.Orders;
import com.foodwant.foodwant.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 訂單資訊
 * @author JIA-YANG, LAI
 * @create 2022-10-17 下午 04:29
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 下單
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("訂單數據: {}",orders);
        orderService.submit(orders);
        return R.success("下單成功");

    }

    /**
     * 後台的訂單明細分頁
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, String number){
        Page<Orders> ordersPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(ordersPage, queryWrapper);
        return R.success(ordersPage);

    }

    /**
     * 前台的最新訂單分頁
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userpage(int page, int pageSize){
        Page<OrdersDto>  orderDetailPage= orderService.getOrderDetailPage(page,pageSize);
        return R.success(orderDetailPage);

    }



}

