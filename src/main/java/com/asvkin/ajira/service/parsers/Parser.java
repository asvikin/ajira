package com.asvkin.ajira.service.parsers;

public interface Parser {
	<T> T parse(String data, Class<T> returnType);
}
