package com.vince.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vince.dao.UserDao;
import com.vince.domain.User;
import com.vince.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService{
}
