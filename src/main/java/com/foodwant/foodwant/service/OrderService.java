package com.foodwant.foodwant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.foodwant.foodwant.dto.OrdersDto;
import com.foodwant.foodwant.entity.OrderDetail;
import com.foodwant.foodwant.entity.Orders;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-17 下午 04:27
 */
public interface OrderService extends IService<Orders> {

    /**
     * 下單
     * @param orders
     */
    public void submit(Orders orders);

    public int getCartCount();

    Page<OrdersDto> getOrderDetailPage(int page, int pageSize);

}
