package com.foodwant.foodwant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.foodwant.foodwant.entity.AddressBook;
import com.foodwant.foodwant.mapper.AddressBookMapper;
import com.foodwant.foodwant.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-11 上午 04:53
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
