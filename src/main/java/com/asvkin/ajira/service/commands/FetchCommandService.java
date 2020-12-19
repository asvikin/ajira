package com.asvkin.ajira.service.commands;

import com.asvkin.ajira.beans.Device;
import com.asvkin.ajira.beans.ResponseBean;
import com.asvkin.ajira.exception.InvalidCommandException;
import com.asvkin.ajira.service.graphs.GraphService;
import com.asvkin.ajira.service.parsers.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Component("FETCH-SERVICE")
public class FetchCommandService extends CommandService {
	private static final String DEVICES = "/devices";
	private static final String INFO_ROUTES = "/info-routes";
	@Autowired
	private GraphService graphService;
	
	@Override
	public ResponseBean processData(Map<String, String> headers, String endPoint, String data) {
		if (endPoint == null || endPoint.isEmpty()) {
			throw new InvalidCommandException("Invalid Command");
		}
		if (endPoint.startsWith(DEVICES)) {
			return fetchAllDevices(headers, data);
		} else if (endPoint.startsWith(INFO_ROUTES)) {
			return fetchRoutesInfo(headers, data, endPoint);
		} else {
			throw new InvalidCommandException("Invalid Command");
		}
	}
	
	private ResponseBean fetchRoutesInfo(Map<String, String> headers, String data, String endPoint) {
		Map<String, String> queryParams = Parser.getQueryParams(endPoint);
		String path = graphService.getpath(queryParams.get("from"), queryParams.get("to"));
		return new ResponseBean("Route is " + path);
	}
	
	private ResponseBean fetchAllDevices(Map<String, String> headers, String data) {
		Set<Device> devices = graphService.getAllDevices();
		ResponseBean responseBean = new ResponseBean();
		responseBean.setDevices(new ArrayList<>(devices));
		return responseBean;
	}
}
