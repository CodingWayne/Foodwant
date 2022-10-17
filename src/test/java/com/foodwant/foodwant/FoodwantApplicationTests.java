package com.foodwant.foodwant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodwant.foodwant.entity.Category;
import com.foodwant.foodwant.entity.ShoppingCart;
import com.foodwant.foodwant.mapper.CategoryMapper;
import com.foodwant.foodwant.service.CategoryService;
import com.foodwant.foodwant.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class FoodwantApplicationTests {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ShoppingCartService shoppingCartService;


    @Test
    public void contextLoads() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.select("name");
        List<Category> categoryList = categoryMapper.selectList(wrapper);
        for (Category category : categoryList) {
            System.out.println(category);
        }
    }

    @Test
    public void test2(){
        Consumer<String> con = System.out::println;
        con.accept("1");
    }

    @Test
    public void test1(){
        byte a=1;

        a+=4;

        System.out.println(a);
        try {
            System.out.println("1");
            System.out.println(1/0);
            System.out.println("2");
        } catch (Exception e) {
            System.out.println("catch");
            e.printStackTrace();
        } finally {
            System.out.println("finally");
        }

        System.out.println("continue--");
    }

    @Test
    public void test3(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1581852670761693185L);
        shoppingCart.setName("滷肉飯");
        shoppingCart.setImage("76f4e168-f78d-4427-8a9f-0f6b09d0e746.jpg");

        shoppingCart.setUserId(1580939510500360194L);
        shoppingCart.setDishId(1580952290846609409L);
        shoppingCart.setDishFlavor("重辣");
        shoppingCart.setAmount(BigDecimal.valueOf(30));

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        for(ShoppingCart i:shoppingCartList){
            if(shoppingCart.getDishFlavor().equals(i.getDishFlavor())){
                System.out.println("口味相同");

            }
        }
        System.out.println("口味不相同");

    }
    @Test
    public void test4(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1581839879455670275L);
        shoppingCart.setName("滷肉飯");
        shoppingCart.setImage("76f4e168-f78d-4427-8a9f-0f6b09d0e746.jpg");

        shoppingCart.setUserId(1580939510500360194L);
        shoppingCart.setDishId(1580952290846609409L);
        shoppingCart.setDishFlavor("中辣");
        shoppingCart.setAmount(BigDecimal.valueOf(30));
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartService.save(shoppingCart);


    }

}
