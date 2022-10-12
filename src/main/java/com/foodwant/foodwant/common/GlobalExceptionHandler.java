package com.foodwant.foodwant.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全域異常處理
 * @author JIA-YANG, LAI
 * @create 2022-10-08 上午 01:03
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//攔截@Controller
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exception(SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知錯誤");
    }


    /**
     * 用來捕獲CategoryServiceImpl中無法刪除菜品或套餐的異常
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exception(CustomException ex){
        log.info(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
