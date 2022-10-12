package com.foodwant.foodwant;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.foodwant.foodwant.entity.Category;
import com.foodwant.foodwant.mapper.CategoryMapper;
import com.foodwant.foodwant.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

}
