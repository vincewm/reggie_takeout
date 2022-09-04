package com.vince.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vince.common.R;
import com.vince.domain.User;
import com.vince.service.UserService;
import com.vince.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("手机号：{}，验证码：{}",user.getPhone(),code);
        redisTemplate.opsForValue().set(user.getPhone(),code,5, TimeUnit.MINUTES);
        return R.success("发送成功");
    }
    @PostMapping("/login")
    public R<String> login(@RequestBody Map<String,String> map, HttpSession session){
        String phone = map.get("phone");
        String code = map.get("code");
        Object sessionCode =redisTemplate.opsForValue().get(phone);
        log.info("提交的手机号：{}，验证码：{}，session中的验证码：{}",phone,code,sessionCode);
        if(sessionCode!=null&&sessionCode.equals(code)){
            redisTemplate.delete(phone);
            //验证成功，查询是否已经注册
            log.info("验证成功");
            LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);
            //没查到，未注册
            if(user==null){
                log.info("没查到，未注册");
                User addUser = new User();
                addUser.setPhone(phone);addUser.setStatus(1);
                if(!userService.save(addUser)) return R.error("验证成功，自动注册失败");
                session.setAttribute("user",addUser.getId());
                return R.success("验证成功，自动注册成功");
            }else {
                //已注册
                log.info("已经注册，正在登录");
                session.setAttribute("user",user.getId());
                return R.success("验证成功，登录成功");
            }
        }else {
            log.info("验证失败，此时的code：{}",code);
            return R.error("验证失败");
        }
    }
}
