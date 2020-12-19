package com.asvkin.ajira.service.graphs;

import com.asvkin.ajira.beans.ConnectionBean;
import com.asvkin.ajira.beans.Device;
import com.asvkin.ajira.exception.AlreadyExistsException;
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
		List<String> targets = connectionBean.getTargets();
		for (String target : targets) {
			if (connectionBean.getSource().equals(target)) {
				throw new UnsupportedOperationException("Cannot Connect to device to itself");
			}
			graph.addEdge(graph.getDeviceWithName(connectionBean.getSource()).get(),
					graph.getDeviceWithName(target).get(), true);
		}
	}
	
	public Set<Device> getAllDevices() {
		return graph.getVertex();
	}
	
	public String getpath(String from, String to) {
		return null;
	}
	
	public void modifyStrength(String deviceName) {
	}
}
