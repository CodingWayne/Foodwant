package com.foodwant.foodwant.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
/**
 * 用戶資訊
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    //姓名
    private String name;


    //手機號碼
    private String phone;


    //性別 0 女 1 男
    private String sex;


    //身份證字號
    private String idNumber;


    //頭像
    private String avatar;


    //狀態 0:禁用，1:正常
    private Integer status;
}
