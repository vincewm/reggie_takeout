package com.vince.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vince.dao.AddressBookDao;
import com.vince.domain.AddressBook;
import com.vince.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao,AddressBook> implements AddressBookService {
}
