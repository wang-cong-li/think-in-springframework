package com.gupao.vip.myspringframework.v0.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD})
@Documented
public @interface Autowired {
    String value() default "";
}
