package org.clc.server.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.clc.common.context.BaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @version 1.0
 */
@Aspect
@Component
public class PostManagementLoggingAspect {
    //TODO:AOP实现帖子操作日志记录
    private static final Logger logger = LoggerFactory.getLogger(PostManagementLoggingAspect.class);

    // 定义一个切入点，这里选择所有在PostService中的公共方法
    @Pointcut("execution(* org.clc.server.service.PostService.*(..))")
    public void postServiceMethods() {}

    // 在方法执行前记录日志
    @Before("postServiceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        String uid= BaseContext.getCurrentId();
        logger.info("Entering in Method : " + methodName + " with arguments : " + Arrays.toString(methodArgs) + " by user: " + uid);
    }

    // 在方法执行后记录日志
    @After("postServiceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String uid= BaseContext.getCurrentId();
        logger.info("Exiting from Method : " + methodName + " by user: " + uid);
    }
}
