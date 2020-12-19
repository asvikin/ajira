package com.asvkin.ajira.service.commands;

import com.asvkin.ajira.beans.ResponseBean;
import com.asvkin.ajira.exception.InvalidContentTypeException;
import com.asvkin.ajira.service.parsers.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public abstract class CommandService {
	@Autowired
	private ApplicationContext applicationContext;
	
	public abstract ResponseBean processData(Map<String, String> headers, String endPoint, String data);
	
	protected <T> T parseInput(String data, String mediaType, Class<T> returnType) {
		return getParser(mediaType).parse(data, returnType);
	}
	
	private Parser getParser(String mediaType) {
		try {
			return (Parser) applicationContext.getBean("parser-" + mediaType);
		} catch (Exception e) {
			throw new InvalidContentTypeException("Invalid Command");
		}
	}
}
