package com.gupao.vip.myspringframework.v0.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
public @interface RequestMapping {
    public String value() default "";
}
