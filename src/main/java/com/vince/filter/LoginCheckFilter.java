package com.vince.filter;

import com.alibaba.fastjson2.JSON;
import com.vince.common.BaseContext;
import com.vince.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String[] notFilter = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        String nowUri = request.getRequestURI();
//        放行不需要拦截的uri
        for(String url:notFilter){
            if(PATH_MATCHER.match(url, nowUri)){
                filterChain.doFilter(request,response);
                return;
            }
        }
//        session里有employee则已登录，放行。针对于后端
        if(request.getSession().getAttribute("employee")!=null){
            BaseContext.setThreadId((Long) request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);

            return;
        }
        //session里有user则已登录，放行。针对于前端
        if(request.getSession().getAttribute("user")!=null){
            BaseContext.setThreadId((Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }
//        未登录返回错误信息，前端设置跳转
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }
}
