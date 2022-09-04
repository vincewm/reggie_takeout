package com.vince;

import com.vince.domain.Dish;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


@SpringBootTest
@Slf4j
class ReggieApplicationTests {
    @Test
    void test() {
    }

}
