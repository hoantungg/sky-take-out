package com.sky.exception;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> exceptionHandler(DuplicateKeyException ex){
        log.error("数据库唯一键冲突",ex);
        String message = ex.getMostSpecificCause().getMessage();

        if(message != null && message.contains("Duplicate entry")){
            String[] messageParts = message.split(" ");
            if(messageParts.length > 2){
                String duplicateValue = messageParts[2];
                return Result.error(duplicateValue + MessageConstant.ALREADY_EXISTS);
            }
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);


    }
}
