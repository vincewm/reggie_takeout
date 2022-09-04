package com.vince.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> SQLExceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.info("异常:"+exception);
        if(exception.getMessage().contains("Duplicate entry"))
        return R.error("唯一约束异常:"+exception.getMessage().split(" ")[2]+"已存在");
        else return R.error("未知SQLIntegrityConstraintViolationException错误");
    }
    @ExceptionHandler(CustomException.class)
    public R<String> customExceptionHandler(CustomException exception){
        log.info("异常：{}",exception.getMessage());
        return R.error(exception.getMessage());
    }
}
