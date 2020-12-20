package com.asvkin.ajira.api.annotations;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiRequest {
	String path() default "";
	
	ApiRequestType method() default ApiRequestType.FETCH;
	
	@AllArgsConstructor
	@Getter
	enum ApiRequestType {
		FETCH("FETCH"),
		MODIFY("MODIFY"),
		CREATE("CREATE");
		private static final Map<String, ApiRequestType> lookup = new HashMap<>();
		
		static {
			for (ApiRequestType requestType : ApiRequestType.values()) {
				lookup.put(requestType.getMethod(), requestType);
			}
		}
		
		private final String method;
		
		public static Optional<ApiRequestType> requestTypeOf(String type) {
			return Optional.ofNullable(lookup.get(type));
		}
	}
}
