package org.clc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义注解处理器将识别的单个响应注解
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MyApiResponse {
    String responseCode();
    String description();
}
