package com.asvkin.ajira.service.graphs;

import com.asvkin.ajira.beans.ConnectionBean;
import com.asvkin.ajira.beans.Device;
import com.asvkin.ajira.beans.ModifyStrengthBean;
import com.asvkin.ajira.constants.DeviceType;
import com.asvkin.ajira.exception.AlreadyExistsException;
import com.asvkin.ajira.exception.InvalidCommandException;
import com.asvkin.ajira.exception.NotFoundException;
import com.asvkin.ajira.exception.UnSupportedDeviceTypeException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class GraphService {
	final private DeviceGraph graph = new DeviceGraph();
	
	public void addNewDevice(Device device) {
		if (graph.hasVertex(device)) {
			throw new AlreadyExistsException("Device '" + device.getName() + "' already exists");
		}
		graph.addVertex(device);
	}
	
	public void addConnections(ConnectionBean connectionBean) {
		if (connectionBean.getSource() == null || connectionBean.getSource().isEmpty() || connectionBean.getTargets() == null || connectionBean.getTargets().isEmpty()) {
			throw new InvalidCommandException("Invalid command Syntax");
		}
		List<String> targets = connectionBean.getTargets();
		for (String target : targets) {
			if (connectionBean.getSource().equals(target)) {
				throw new UnSupportedDeviceTypeException("Cannot Connect to device to itself");
			}
			if (graph.hasEdge(graph.getDeviceWithName(connectionBean.getSource()).orElseThrow(() -> new NotFoundException(
							"Device Not Found")),
					graph.getDeviceWithName(target).orElseThrow(() -> new NotFoundException("Device Not Found")))) {
				throw new AlreadyExistsException("Device are already Connected");
			}
			graph.addEdge(graph.getDeviceWithName(connectionBean.getSource()).orElseThrow(() -> new NotFoundException(
							"Device Not Found")),
					graph.getDeviceWithName(target).orElseThrow(() -> new NotFoundException("Device Not Found")),
					true);
		}
	}
	
	public Set<Device> getAllDevices() {
		return graph.getVertex();
	}
	
	public String getPath(String from, String to) {
		return graph.getShortestPath(from, to);
	}
	
	public void modifyStrength(ModifyStrengthBean modifyStrengthBean, String deviceName) {
		Device device = graph.getDeviceWithName(deviceName).orElseThrow(() -> new NotFoundException("Device Not " +
				                                                                                            "Found"));
		if (DeviceType.valueOf(device.getType()) == DeviceType.REPEATER) {
			throw new UnSupportedDeviceTypeException("Cannot set strength for a Repeater");
		}
		if (modifyStrengthBean.getValue() < 0) {
			throw new InvalidCommandException("Value should be a Positive Integer");
		}
		device.setStrength(modifyStrengthBean.getValue());
	}
}
