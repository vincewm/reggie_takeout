package com.vince.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vince.common.R;
import com.vince.domain.Employee;
import com.vince.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

//@RestController里包含了@ResponseBody
@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String username=employee.getUsername();
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getPassword,password);
        wrapper.eq(Employee::getUsername,username);
        Employee queryEmployee = employeeService.getOne(wrapper);
        if(queryEmployee==null){
            return R.error("用户名或密码错误");
        }
        if(queryEmployee.getStatus()==0){
            return R.error("账号已被禁用");
        }
        request.getSession().setAttribute("employee",queryEmployee.getId());
        log.info("登陆成功："+queryEmployee);
        return R.success(queryEmployee);
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //尝试删除
        try {
            request.getSession().removeAttribute("employee");
        }catch (Exception e){
            //删除失败
            return R.error("登出失败");
        }
        return R.success("登出成功");
    }

    /**
     * 增加员工
     * @param employee
     * @param request
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        log.info("{}",employee);
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeService.save(employee);
        return R.success("登陆成功");
    }

    /**
     * 分页展示员工
     * @param currentPage
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> getByPage(@RequestParam("page") Integer currentPage,Integer pageSize,String name){
        Page page = new Page(currentPage, pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null&&name.length()>0,Employee::getName,name);
        wrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(page,wrapper);
        return R.success(page);
    }

    /**
     * 更新员工
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
//        更新时间和操作者id会在自定义元数据对象控制类MyMetaObjectHandler类里自动填充
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 更新回显员工
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        log.info("查询到员工：{}",employee);
        if(employee!=null)
        return R.success(employee);
        else return R.error("找不到这个员工");
    }
}
