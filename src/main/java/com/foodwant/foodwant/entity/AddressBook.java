package com.foodwant.foodwant.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 地址簿
 */
@Data
public class AddressBook implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    //用戶id
    private Long userId;


    //收貨人
    private String consignee;


    //手機號碼
    private String phone;


    //性別 0 女 1 男
    private String sex;



    private String provinceCode;



    private String provinceName;



    private String cityCode;



    private String cityName;



    private String districtCode;



    private String districtName;


    //詳細地址
    private String detail;


    //標籤
    private String label;

    //是否默認 0 否 1是
    private Integer isDefault;

    //創建時間
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    //更新時間
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    //創建人
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    //修改人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;


    //是否刪除
    private Integer isDeleted;
}
