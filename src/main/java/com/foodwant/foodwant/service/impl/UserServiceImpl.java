package com.foodwant.foodwant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.foodwant.foodwant.entity.User;
import com.foodwant.foodwant.mapper.UserMapper;
import com.foodwant.foodwant.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-14 下午 06:59
 */
@Service
@Slf4j
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    /**
     * 發送驗證信
     * @param code
     */
    @Override
    public void sendmail(String mail,Integer code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("a0931228191@gmail.com");
        simpleMailMessage.setTo(mail);
        simpleMailMessage.setSubject("Foodwant登入驗證");
        simpleMailMessage.setText("驗證碼為: "+ code);
        mailSender.send(simpleMailMessage);
        log.info("{} 已寄出驗證信至 {}",simpleMailMessage.getFrom(),simpleMailMessage.getTo());



    }
}
