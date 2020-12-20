package com.asvkin.ajira.api.controllers;

import com.asvkin.ajira.api.annotations.*;
import com.asvkin.ajira.beans.ConnectionBean;
import com.asvkin.ajira.beans.Device;
import com.asvkin.ajira.beans.ModifyStrengthBean;
import com.asvkin.ajira.beans.ResponseBean;
import com.asvkin.ajira.constants.DeviceType;
import com.asvkin.ajira.exception.InvalidCommandException;
import com.asvkin.ajira.exception.UnSupportedDeviceTypeException;
import com.asvkin.ajira.service.graphs.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Set;

@ApiRequestController
@SuppressWarnings("unused")
@Slf4j
public class CommandController {
	@Autowired
	private GraphService graphService;
	
	@ApiRequest(path = "/devices", method = ApiRequest.ApiRequestType.CREATE)
	public ResponseBean createDevice(@ApiRequestBody Device device) {
		if (DeviceType.lookUp(device.getType()).isEmpty()) {
			throw new UnSupportedDeviceTypeException("type '" + device.getType() + "' is not supported");
		}
		graphService.addNewDevice(device);
		return new ResponseBean("Successfully added " + device.getName());
	}
	
	@ApiRequest(path = "/connections", method = ApiRequest.ApiRequestType.CREATE)
	public ResponseBean createConnection(@ApiRequestBody ConnectionBean connectionBean) {
		if (connectionBean == null) {
			throw new InvalidCommandException("Invalid Command");
		}
		graphService.addConnections(connectionBean);
		return new ResponseBean("Successfully connected");
	}
	
	@ApiRequest(path = "/devices", method = ApiRequest.ApiRequestType.FETCH)
	public ResponseBean getAllDevices() {
		Set<Device> devices = graphService.getAllDevices();
		ResponseBean responseBean = new ResponseBean();
		responseBean.setDevices(new ArrayList<>(devices));
		return responseBean;
	}
	
	@ApiRequest(path = "/info-routes", method = ApiRequest.ApiRequestType.FETCH)
	public ResponseBean getRoutes(@ApiQueryParameter("from") String from, @ApiQueryParameter("to") String to) {
		String path = graphService.getPath(from, to);
		return new ResponseBean("Route is " + path);
	}
	
	@ApiRequest(path = "/devices/{deviceName}/strength", method = ApiRequest.ApiRequestType.MODIFY)
	public ResponseBean modifyStrength(@ApiPathVariable("deviceName") String deviceName,
			@ApiRequestBody ModifyStrengthBean modifyStrengthBean) {
		graphService.modifyStrength(modifyStrengthBean, deviceName);
		return new ResponseBean("Successfully defined strength");
	}
	
	
}
