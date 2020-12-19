package com.asvkin.ajira.service.graphs;

import com.asvkin.ajira.beans.AbstractGraph;
import com.asvkin.ajira.beans.Device;

import java.util.Optional;
import java.util.Set;

public class DeviceGraph extends AbstractGraph<Device> {
	public Optional<Device> getDeviceWithName(String name) {
		Set<Device> devices = map.keySet();
		for (Device device : devices) {
			if (name.equals(device.getName())) {
				return Optional.of(device);
			}
		}
		return Optional.empty();
	}
}
