package com.foodwant.foodwant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodwant.foodwant.common.R;
import com.foodwant.foodwant.entity.Employee;
import com.foodwant.foodwant.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-07 下午 08:01
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登入
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){//接收login傳來的json帳密
        // 1. 將頁面提交的密碼password進行md5加密處理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());//明文進行md5加密

        // 2. 根據頁面提交的username查詢數據庫
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 如果沒有則顯示登入失敗
        if(emp == null){
            return R.error("登入失敗");
        }

        //4. 密碼比對，不一致則顯示登入失敗
        //明文相同，md5加密結果也相同，且DB中存放的是加密後結果，因此只要一樣便表示密碼相同
        if(!emp.getPassword().equals(password)){
            return R.error("登入失敗");
        }

        //5. 觀察員工狀態，如禁用則顯示已禁用
        if(emp.getStatus() == 0){
            return R.error("該員工已禁用");
        }

        //6. 登入成功，將員工id存入Session並返回登入成功
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 員工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        //清理Session中保存的當前登入的員工的id
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     * 新增員工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增員工: {}",employee.toString());

        //替每一位員工設置初始密碼123456，此密碼需要使用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //紀錄建立員工的時間
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //為了記錄是誰增加這個員工，需要獲得當前登入使用著的id
        Long empId = (Long) request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增成功");
    }


    /**
     * 分頁查詢
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}",page,pageSize,name);

        //分頁物件
        Page pageInfo = new Page(page,pageSize);

        //條件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //排序條件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //執行查詢
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改員工資訊
     * @param request
     * @param employee
     * @return
     */
    @PutMapping()
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        Long empId = (Long) request.getSession().getAttribute("employee");
        log.info(employee.toString());
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        long id = Thread.currentThread().getId();
        log.info("目前線程id為: {}",id);
        return R.success("員工資訊修改成功");
    }

    /**
     * 根據id查詢員工資訊
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根據id查詢員工資訊");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("無此員工");
    }



}
