package com.gupao.vip.myspringframework.v0.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Service {
    String value() default "";
}
