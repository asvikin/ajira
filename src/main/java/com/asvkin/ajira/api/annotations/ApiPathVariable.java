package com.asvkin.ajira.api.annotations;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiPathVariable {
	String value();
	
	boolean required() default true;
}
