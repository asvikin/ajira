package com.asvkin.ajira.api.annotations;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiRequestBody {
	boolean required() default true;
}
