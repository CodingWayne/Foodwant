package com.foodwant.foodwant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.foodwant.foodwant.common.BaseContext;
import com.foodwant.foodwant.common.R;
import com.foodwant.foodwant.common.UserBaseContext;
import com.foodwant.foodwant.entity.AddressBook;
import com.foodwant.foodwant.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-11 上午 04:54
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(UserBaseContext.getCurrentId());

        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AddressBook::getUpdateUser,UserBaseContext.getCurrentId()).set(AddressBook::getCreateUser,UserBaseContext.getCurrentId()).eq(AddressBook::getUserId,UserBaseContext.getCurrentId());
        addressBookService.update(updateWrapper);
        return R.success(addressBook);
    }

    /**
     * 設定默認地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, UserBaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根據id查地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("沒有找到地址");
        }
    }

    /**
     * 查默認地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, UserBaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("沒有找到地址");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查指定用戶的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(UserBaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        //條件
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }

    /**
     * 更改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);



        return R.success("已修改地址");
    }

    /**
     * 刪除地址
     * @param
     * @return
     */
    @DeleteMapping
    public R<String> remove(Long ids){

        addressBookService.removeById(ids);

        return R.success("已刪除地址");

    }
}
