package com.foodwant.foodwant.dto;

import com.foodwant.foodwant.entity.OrderDetail;
import com.foodwant.foodwant.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-17 下午 09:23
 */
@Data
public class OrdersDto extends Orders {
    List<OrderDetail> orderDetails;
}
