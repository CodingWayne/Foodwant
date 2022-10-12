package com.foodwant.foodwant.common;

/**
 * 自定義業務異常
 * @author JIA-YANG, LAI
 * @create 2022-10-09 上午 02:42
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
