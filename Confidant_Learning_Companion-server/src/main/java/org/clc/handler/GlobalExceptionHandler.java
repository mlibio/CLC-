package org.clc.handler;

import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.clc.constant.MessageConstant;
import org.clc.exception.BaseException;
import org.clc.exception.DuplicatePhoneException;
import org.clc.exception.JwtAuthenticationException;
import org.clc.result.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(500,ex.getMessage());
    }

    /**
     * 处理SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        log.error(message);
        if(message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String key = split[5];
            String msg=MessageConstant.ALREADY_EXISTS;
            if(key.equals("'learner.uni_email'")){
                msg = MessageConstant.EMAIL_ALREADY_EXISTS;
            }
            return Result.error(500,msg);
        }else{
            return Result.error(500,MessageConstant.UNKNOWN_ERROR);
        }
    }
    /**
     * 参数校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result argumentExceptionHandler(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError().getDefaultMessage();
        log.info("发生参数异常:{}", message);
        return Result.error(400,message);
    }
    /**
     * JWT令牌校验异常
     */
    @ExceptionHandler(value = JwtAuthenticationException.class)
    public Result JwtAuthenticationExceptionHandler(JwtAuthenticationException exception) {
        String message = exception.getMessage();
        log.info("JWT令牌校验失败:{}",message);
        return Result.error(401,MessageConstant.TOKEN_VALIDATION_FAILED+":"+message);
    }
    /**
     * 手机号重复异常
     */
    @ExceptionHandler(value = DuplicatePhoneException.class)
    public Result DuplicatePhoneException(DuplicatePhoneException exception) {
        String message = exception.getMessage();
        log.info(message);
        return Result.error(401,message);
    }

    /**
     * 资源不存在异常
     * @param exception
     * @return
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException exception) {
        String message = exception.getMessage();
        log.info(message);
        return Result.error(404,MessageConstant.NO_RESOURCES_EXIST+":"+message);
    }

    /**
     * 类型错误异常
     * @param exception
     * @return
     */
    @ExceptionHandler(value = UnexpectedTypeException.class)
    public Result unexpectedTypeExceptionHandler(UnexpectedTypeException exception) {
        String message = exception.getMessage();
        log.info("发生类型错误异常:{}", message);
        return Result.error(400,message);
    }
}
