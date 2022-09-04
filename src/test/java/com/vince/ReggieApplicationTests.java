package com.vince;

import com.vince.service.CategoryService;
import com.vince.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
class ReggieApplicationTests {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderService orderService;
    @Test
    void test() {

        System.out.println(orderService.list());
    }

}
