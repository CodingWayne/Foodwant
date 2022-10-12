package com.foodwant.foodwant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.foodwant.foodwant.entity.User;
import com.foodwant.foodwant.mapper.UserMapper;
import com.foodwant.foodwant.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-11 上午 03:49
 */
@Service
@Slf4j
public class service extends ServiceImpl<UserMapper, User> implements UserService {
}
