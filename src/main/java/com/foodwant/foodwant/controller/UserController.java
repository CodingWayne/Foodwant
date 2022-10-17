package com.foodwant.foodwant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.foodwant.foodwant.common.R;
import com.foodwant.foodwant.entity.User;
import com.foodwant.foodwant.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-11 上午 03:50
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 發送手機驗證碼
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){

        String mail = user.getMail();
        log.info("mail: {}",mail);
        log.info(mail);
        if(StringUtils.isNotEmpty(mail)){
            //產生4位驗證碼
            Integer code = new Random().nextInt(9999);
            if(code < 1000){
                code = code + 1000;//保證位數>4
            }
            log.info("code={}",code);
            session.setAttribute(mail,String.valueOf(code));


            userService.sendmail(mail,code);
            //將驗證碼存到Session

            return R.success("驗證碼發送成功");
        }
        return R.error("簡訊傳送失敗");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info(map.toString());

        String mail = map.get("mail").toString();

        String code = map.get("code").toString();
        //從Session中獲得驗證碼
        String codeInSession = (String) session.getAttribute(mail);//預設驗證碼
        log.info("codeInSession= {}",codeInSession);


        //驗證碼比對
        if(codeInSession != null && codeInSession.equals(code)){

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getMail,mail);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判斷是否為新用戶，否則建立DB
                user = new User();
                user.setMail(mail);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            log.info("已插入Session:{},{}","user",user.getId());
            return R.success(user);
        }
        return R.error("登入失敗");
    }

    /**
     * 用戶退出
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){

        //清理Session中保存的當前登入的用戶的id
        request.getSession().removeAttribute("user");

        return R.success("退出成功");
    }




}
