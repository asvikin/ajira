package com.asvkin.ajira.service;

import com.asvkin.ajira.beans.ResponseBean;
import com.asvkin.ajira.exception.InvalidCommandException;
import com.asvkin.ajira.service.commands.CommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class AjiraNetService {
	static private final String NEW_LINE = "\r\n";
	static private final String DOUBLE_NEW_LINE = "\r\n\r\n";
	static private final String SPACE = " ";
	@Autowired
	ApplicationContext applicationContext;
	
	public ResponseBean processData(String requestData) {
		log.info("Request Data : {}", requestData);
		CommandService commandService = getCommandService(requestData);
		return commandService.processData(getHeaders(requestData), getEndPoint(requestData), getData(requestData));
	}
	
	private Map<String, String> getHeaders(String input) {
		try {
			int newLineIndex = input.indexOf(NEW_LINE);
			int doubleNewLineIndex = input.indexOf(DOUBLE_NEW_LINE);
			String headerAsString = input.substring(newLineIndex + 1, doubleNewLineIndex);
			return getHeadersAsMap(headerAsString.split(NEW_LINE));
		} catch (Exception e) {
			throw new InvalidCommandException("Invalid Command", e.getCause());
		}
	}
	
	private Map<String, String> getHeadersAsMap(String[] headersArray) {
		Map<String, String> headers = new HashMap<>();
		for (String header : headersArray) {
			int colonIndex = header.indexOf(":");
			headers.put(header.substring(0, colonIndex).trim(), header.substring(colonIndex + 1).trim());
		}
		return headers;
	}
	
	private String getEndPoint(String input) {
		try {
			return Objects.requireNonNull(input.substring(0, input.indexOf(NEW_LINE)), "Invalid Command").split(SPACE)[1];
		} catch (Exception e) {
			throw new InvalidCommandException("Invalid Command", e.getCause());
		}
	}
	
	private CommandService getCommandService(String input) {
		String command = Objects.requireNonNull(input, "Invalid Command").split(SPACE)[0];
		try {
			return (CommandService) applicationContext.getBean(command + "-SERVICE");
		} catch (Exception e) {
			throw new InvalidCommandException("Invalid Command - " + command, e.getCause());
		}
	}
	
	private String getData(String input) {
		String[] split = Objects.requireNonNull(input, "Invalid Command").split(DOUBLE_NEW_LINE);
		return String.join(DOUBLE_NEW_LINE, Arrays.copyOfRange(split, 1, split.length));
	}
}
/*
CREATE /path/path2
header-key1: header-value1
header-key2: header-value2

data.. \n
 */