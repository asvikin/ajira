package com.asvkin.ajira.service.commands;

import com.asvkin.ajira.beans.ModifyStrengthBean;
import com.asvkin.ajira.beans.ResponseBean;
import com.asvkin.ajira.exception.InvalidCommandException;
import com.asvkin.ajira.service.graphs.GraphService;
import com.asvkin.ajira.service.parsers.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("MODIFY-SERVICE")
public class ModifyCommandService extends CommandService {
	private static final String DEVICES = "/devices";
	@Autowired
	private GraphService graphService;
	
	@Override
	public ResponseBean processData(Map<String, String> headers, String endPoint, String data) {
		if (endPoint == null || endPoint.isEmpty()) {
			throw new InvalidCommandException("Invalid Command");
		}
		if (endPoint.startsWith(DEVICES)) {
			return modifyDeviceStrength(headers, data, endPoint);
		} else {
			throw new InvalidCommandException("Invalid Command");
		}
	}
	
	private ResponseBean modifyDeviceStrength(Map<String, String> headers, String data, String endPoint) {
		ModifyStrengthBean modifyStrengthBean = parseInput(data, headers.get("content-type"),
				ModifyStrengthBean.class);
		if (modifyStrengthBean == null) {
			throw new InvalidCommandException("Invalid Command");
		}
		String uriTemplate = "/devices/{deviceName}/strength";
		Map<String, String> pathParams = Parser.getPathParams(endPoint, uriTemplate);
		String deviceName = pathParams.get("deviceName");
		graphService.modifyStrength(modifyStrengthBean, deviceName);
		return new ResponseBean("Successfully defined strength");
	}
}
