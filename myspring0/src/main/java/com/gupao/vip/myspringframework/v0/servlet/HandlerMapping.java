package com.gupao.vip.myspringframework.v0.servlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerMapping {
    private Object instance;

    private Method method;

    private Object[] invokeParams;

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getInvokeParams() {
        return invokeParams;
    }

    public void setInvokeParams(Object[] invokeParams) {
        this.invokeParams = invokeParams;
    }

    public Object handleRequest() throws InvocationTargetException, IllegalAccessException {
        return this.method.invoke(this.instance,this.invokeParams);
    }
}
