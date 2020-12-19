package com.asvkin.ajira.service.commands;

import com.asvkin.ajira.beans.ConnectionBean;
import com.asvkin.ajira.beans.Device;
import com.asvkin.ajira.beans.ResponseBean;
import com.asvkin.ajira.constants.DeviceType;
import com.asvkin.ajira.exception.InvalidCommandException;
import com.asvkin.ajira.exception.UnSupportedDeviceTypeException;
import com.asvkin.ajira.service.graphs.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("CREATE-SERVICE")
@Slf4j
public class CreateCommandService extends CommandService {
	final static private String DEVICES = "/devices";
	private static final String CONNECTIONS = "/connections";
	private static final String CONTENT_TYPE = "content-type";
	@Autowired
	private GraphService graphService;
	
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
		ConnectionBean connectionBean = parseInput(data, headers.get(CONTENT_TYPE), ConnectionBean.class);
		if (connectionBean == null) {
			throw new InvalidCommandException("Invalid Command");
		}
		log.info("Create Connections ,{}", connectionBean);
		graphService.addConnections(connectionBean);
		return new ResponseBean("Successfully connected");
	}
	
	private ResponseBean createNewDevice(Map<String, String> headers, String data) {
		Device device = parseInput(data, headers.get(CONTENT_TYPE), Device.class);
		if (DeviceType.lookUp(device.getType()).isEmpty()) {
			throw new UnSupportedDeviceTypeException("type '" + device.getType() + "' is not supported");
		}
		log.info(" Create Device :{}", device);
		graphService.addNewDevice(device);
		return new ResponseBean("Successfully added " + device.getName());
	}
	
}
