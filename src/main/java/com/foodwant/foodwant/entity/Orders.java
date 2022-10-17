package com.foodwant.foodwant.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 訂單
 */
@Data
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //訂單號
    private String number;

    //訂單狀態 1待付款，2待派送，3已派送，4已完成，5已取消
    private Integer status;


    //下單用戶id
    private Long userId;

    //地址id
    private Long addressBookId;


    //下單時間
    private LocalDateTime orderTime;


    //結賬時間
    private LocalDateTime checkoutTime;


    //支付方式 1ATM 2VISA
    private Integer payMethod;


    //實收金額
    private BigDecimal amount;

    //備註
    private String remark;

    //用戶名
    private String userName;

    //手機號
    private String phone;

    //地址
    private String address;

    //收貨人
    private String consignee;


}