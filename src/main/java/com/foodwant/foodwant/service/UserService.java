package com.foodwant.foodwant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.foodwant.foodwant.entity.User;
import org.springframework.stereotype.Service;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-11 上午 03:49
 */
public interface UserService extends IService<User> {
    void sendmail(String mail,Integer code);

}
