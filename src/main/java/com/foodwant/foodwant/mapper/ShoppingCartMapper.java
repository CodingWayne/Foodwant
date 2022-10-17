package com.foodwant.foodwant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.foodwant.foodwant.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-16 下午 07:58
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
