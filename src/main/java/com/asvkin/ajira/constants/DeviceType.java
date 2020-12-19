package com.asvkin.ajira.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum DeviceType {
	COMPUTER("COMPUTER"),
	REPEATER("REPEATER");
	private static final Map<String, DeviceType> lookUp = new HashMap<>();
	
	static {
		for (DeviceType deviceType : DeviceType.values()) {
			lookUp.put(deviceType.getType(), deviceType);
		}
	}
	
	private final String type;
	
	public static Optional<DeviceType> lookUp(String type) {
		return Optional.ofNullable(lookUp.get(type));
	}
}
