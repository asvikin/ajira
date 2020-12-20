package com.asvkin.ajira.api.annotations;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiRequestHeader {
	String value() default "";
	
	boolean required() default true;
	
	String defaultValue() default "";
}
