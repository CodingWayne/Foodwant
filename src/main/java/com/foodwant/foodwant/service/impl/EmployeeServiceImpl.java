package com.foodwant.foodwant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.foodwant.foodwant.entity.Employee;
import com.foodwant.foodwant.mapper.EmployeeMapper;
import com.foodwant.foodwant.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-07 下午 07:58
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{
}
