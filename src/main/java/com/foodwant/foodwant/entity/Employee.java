package com.foodwant.foodwant.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 員工實體
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身分證號碼

    private Integer status;

    @TableField(fill = FieldFill.INSERT)//插入時填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新時填充字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)//插入時填充字段
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新時填充字段
    private Long updateUser;

}
