package com.asvkin.ajira.service.graphs;

import com.asvkin.ajira.beans.AbstractGraph;
import com.asvkin.ajira.beans.Device;
import com.asvkin.ajira.constants.DeviceType;
import com.asvkin.ajira.exception.NotFoundException;
import com.asvkin.ajira.exception.UnSupportedDeviceTypeException;

import java.util.*;

public class DeviceGraph extends AbstractGraph<Device> {
	public static String formatRouteAsReadable(List<String> devices) {
		StringBuilder formatter = new StringBuilder();
		for (int i = 0; i < devices.size() - 1; i++) {
			formatter.append(devices.get(i)).append(" -> ");
		}
		formatter.append(devices.get(devices.size() - 1));
		return formatter.toString();
	}
	
	public Optional<Device> getDeviceWithName(String name) {
		Set<Device> devices = map.keySet();
		for (Device device : devices) {
			if (name.equals(device.getName())) {
				return Optional.of(device);
			}
		}
		return Optional.empty();
	}
	
	private boolean depthFirstSearch(Device current, Device destination, HashSet<String> traversed,
			LinkedList<String> response, int strength) {
		if (strength <= 0) return false;
		if (!map.containsKey(current))
			throw new NotFoundException("Route Not Found!");
		if (map.get(current).contains(destination)) {
			response.add(destination.getName());
			return true;
		}
		for (Device device : map.get(current)) {
			if (!traversed.contains(device.getName())) {
				traversed.add(device.getName());
				response.add(device.getName());
				strength--;
				if (device.getType().equals("REPEATER")) {
					strength *= 2;
				}
				if (depthFirstSearch(device, destination, traversed, response, strength))
					return true;
				else {
					if (device.getType().equals("REPEATER")) {
						strength /= 2;
					}
					strength++;
				}
				traversed.remove(device.getName());
				response.pollLast();
			}
		}
		return false;
	}
	
	public String getShortestPath(String from, String to) {
		Device fromDevice = this.getDeviceWithName(from).orElseThrow(() -> new NotFoundException(from + " device not" +
				                                                                                         " " +
				                                                                                         "found"));
		Device toDevice = this.getDeviceWithName(to).orElseThrow(() -> new NotFoundException(to + " device not " +
				                                                                                     "found"));
		if (DeviceType.valueOf(fromDevice.getType()).equals(DeviceType.REPEATER)
				    || DeviceType.valueOf(toDevice.getType()).equals(DeviceType.REPEATER)) {
			throw new UnSupportedDeviceTypeException("Route cannot be calculated with REPEATER!");
		}
		if (from.equals(to)) return from + " -> " + to;
		HashSet<String> traversed = new HashSet<>();
		traversed.add(fromDevice.getName());
		LinkedList<String> response = new LinkedList<>();
		response.add(fromDevice.getName());
		if (depthFirstSearch(fromDevice, toDevice, traversed, response, fromDevice.getStrength() + 1))
			return formatRouteAsReadable(response);
		else
			throw new NotFoundException("Route Not Found!");
	}
}
