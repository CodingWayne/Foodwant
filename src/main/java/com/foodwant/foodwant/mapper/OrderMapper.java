package com.foodwant.foodwant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodwant.foodwant.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-17 下午 04:26
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
