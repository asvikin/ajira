package com.asvkin.ajira.service.parsers;

import com.asvkin.ajira.exception.DataParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component("parser-application/json")
public class JsonParser implements Parser {
	@Override
	public <T> T parse(String data, Class<T> returnType) {
		try {
			return data != null && !data.isEmpty() ? new ObjectMapper().readValue(data, returnType) : null;
		} catch (JsonProcessingException e) {
			throw new DataParseException(e.getMessage(), e.getCause());
		}
	}
}
