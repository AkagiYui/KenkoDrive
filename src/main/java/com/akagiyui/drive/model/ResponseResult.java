package com.akagiyui.drive.model;

import com.akagiyui.drive.component.ResponseEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 响应包装体
 *
 * @author AkagiYui
 */
@Data
public class ResponseResult<T> {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 消息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;

    /**
     * 通用响应
     *
     * @param code 状态码
     * @param msg  消息
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应体
     */
    public static <T> ResponseResult<T> response(Integer code, String msg, T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 通用响应
     *
     * @param status 状态枚举
     * @return 响应体
     */
    public static ResponseResult<?> response(ResponseEnum status) {
        return response(status.getCode(), status.getMsg(), null);
    }

    /**
     * 通用响应
     *
     * @param status 状态枚举
     * @return 响应体
     */
    public static <T> ResponseResult<T> response(ResponseEnum status, T data) {
        return response(status.getCode(), status.getMsg(), data);
    }

    /**
     * 成功响应
     *
     * @param data 数据
     * @return 响应体
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseEnum success = ResponseEnum.SUCCESS;
        return response(success.getCode(), success.getMsg(), data);
    }

    /**
     * 无数据成功响应
     *
     * @return 响应体
     */
    public static ResponseResult<?> success() {
        return success(null);
    }

    /**
     * 把响应内容写入响应体
     *
     * @param res    响应
     * @param code   状态码
     * @param status 状态枚举
     */
    public static void writeResponse(HttpServletResponse res, HttpStatus code, ResponseEnum status) {
        res.setStatus(code.value());
        res.setContentType("application/json;charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            res.getWriter().write(objectMapper.writeValueAsString(response(status)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把响应内容写入响应体
     *
     * @param res    响应对象
     * @param code   状态码
     * @param object 响应内容
     */
    public static void writeResponse(HttpServletResponse res, HttpStatus code, Object object) {
        res.setStatus(code.value());
        res.setContentType("application/json;charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            res.getWriter().write(objectMapper.writeValueAsString(response(ResponseEnum.SUCCESS, object)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
