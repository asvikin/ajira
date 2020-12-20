package com.asvkin.ajira.api.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiQueryParameter {
	@AliasFor("name")
	String value() default "";
	
	@AliasFor("value")
	String name() default "";
	
	boolean required() default true;
	
	String defaultValue() default "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n";
}
