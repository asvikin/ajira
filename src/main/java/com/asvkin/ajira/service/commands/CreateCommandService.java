package com.asvkin.ajira.service.commands;

import com.asvkin.ajira.beans.ResponseBean;
import com.asvkin.ajira.exception.InvalidCommandException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("CREATE-SERVICE")
public class CreateCommandService extends CommandService {
	final static private String DEVICES = "/devices";
	private static final String CONNECTIONS = "/connections";
	
	@Override
	public ResponseBean processData(Map<String, String> headers, String endPoint, String data) {
		if (endPoint == null || endPoint.isEmpty()) {
			throw new InvalidCommandException("Invalid Command");
		}
		if (endPoint.startsWith(DEVICES)) {
			return createNewDevice(headers, data);
		} else if (endPoint.startsWith(CONNECTIONS)) {
			return createNewConnection(headers, data);
		} else {
			throw new InvalidCommandException("Invalid Command");
		}
	}
	
	private ResponseBean createNewConnection(Map<String, String> headers, String data) {
		return null;
	}
	
	private ResponseBean createNewDevice(Map<String, String> headers, String data) {
		return null;
	}
	
}
