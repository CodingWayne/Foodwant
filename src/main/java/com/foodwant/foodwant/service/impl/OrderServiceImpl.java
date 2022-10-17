package com.foodwant.foodwant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.foodwant.foodwant.common.BaseContext;
import com.foodwant.foodwant.common.CustomException;
import com.foodwant.foodwant.common.UserBaseContext;
import com.foodwant.foodwant.dto.OrdersDto;
import com.foodwant.foodwant.entity.*;
import com.foodwant.foodwant.mapper.OrderMapper;
import com.foodwant.foodwant.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-17 下午 04:27
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;



    @Override
    public void submit(Orders orders) {
        //獲得當前使用者id
        Long userId = UserBaseContext.getCurrentId();
        //查詢當前使用者的購物車數據
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        //如果購物車為空 拋異常
        if(shoppingCartList == null ||shoppingCartList.size() == 0){
            throw new CustomException("購物車為空");
        }
        //查詢使用者數據
        User user = userService.getById(userId);
        //查詢使用者地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("地址為空");
        }

        long orderId = IdWorker.getId();//由MP提供的類獲得訂單號碼

        AtomicInteger amount = new AtomicInteger(0);//保證線程安全
        //計算購物車的總金額&封裝訂單明細
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());//累加(金額*份數) 再轉value
            return orderDetail;
        }).collect(Collectors.toList());


        //向訂單表orders插入數據
        //組裝訂單數據

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);//等待外送
        orders.setAmount(new BigDecimal(amount.get()));//總金額
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);
        //向訂單明細表order_detail插入數據
        orderDetailService.saveBatch(orderDetails);
        //清空購物車
        shoppingCartService.remove(queryWrapper);

    }

    /**
     * 獲得購物車數目
     */
    @Override
    public int getCartCount() {
        //根據員工Id去order表查訂單號碼
        LambdaQueryWrapper<Orders> queryWrapper1 = new LambdaQueryWrapper();
        queryWrapper1.eq(Orders::getUserId, UserBaseContext.getCurrentId());
        Orders orders = this.getOne(queryWrapper1);

        //根據訂單號碼去訂單明細表查數目
        LambdaQueryWrapper<OrderDetail> queryWrapper2 = new LambdaQueryWrapper();
        queryWrapper2.eq(OrderDetail::getOrderId,orders.getNumber());
        int count = orderDetailService.count(queryWrapper2);
        log.info("共{}件商品",count());
        return count;

    }

    @Override
    public Page<OrdersDto> getOrderDetailPage(int page, int pageSize) {
        //根據userId查訂單表對應的訂單
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Orders::getUserId, UserBaseContext.getCurrentId());
        //根據訂單時間排序
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //進行分頁查
        this.page(ordersPage,queryWrapper);
        //取出records
        List<Orders> ordersPageRecords = ordersPage.getRecords();


        //封裝Page<>OrdersDto
        Page<OrdersDto> ordersDtoPage =  new Page<>(page,pageSize);
        //除了records已外進行複製
        List<OrdersDto> ordersDtos = ordersPageRecords.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            //將訂單明細封裝到dto裡
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper();
            queryWrapper1.eq(OrderDetail::getOrderId,item.getNumber());
            List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper1);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;

        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtos);

        return ordersDtoPage;


    }
}
