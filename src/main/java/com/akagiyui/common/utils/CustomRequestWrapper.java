package com.akagiyui.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

/**
 * 自定义请求包装器，允许修改请求参数
 * @author AkagiYui
 */
public class CustomRequestWrapper extends HttpServletRequestWrapper  {

    private final Map<String, String[]> parameterMap;

    public CustomRequestWrapper(HttpServletRequest request) {
        super(request);

        this.parameterMap = new HashMap<>(request.getParameterMap());
    }

    @Override
    public String getParameter(String name) {
        return this.parameterMap.get(name)[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.parameterMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        List<String> names = new ArrayList<>(this.parameterMap.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public String[] getParameterValues(String name) {
        return this.parameterMap.get(name);
    }

    /**
     * 设置请求参数
     * @param name 参数名
     * @param value 参数值
     */
    public void setParameter(String name, String value) {
        this.parameterMap.put(name, new String[]{value});
    }
}
