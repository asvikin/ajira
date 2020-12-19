package com.asvkin.ajira.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Device {
	private String type;
	private String name;
	@JsonIgnore
	private int strength = 5;
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Device) {
			return this.name.equals(((Device) obj).getName());
		}
		return false;
	}
}
