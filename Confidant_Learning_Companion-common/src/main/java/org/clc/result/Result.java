package org.clc.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：200成功，其它数字为失败
    private String msg; //错误信息
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<T>();
        result.code = 200;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error(Integer code,String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = code;
        return result;
    }

    public static <T> Result<T> success(Integer code,String msg,T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.msg = msg;
        result.code = code;
        return result;
    }
}
