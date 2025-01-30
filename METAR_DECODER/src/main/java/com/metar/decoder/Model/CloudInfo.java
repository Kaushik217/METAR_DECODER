package com.metar.decoder.Model;

public class CloudInfo {
	private String type;
	private String altitude;

	public CloudInfo(String encodedCloudInfo, String type) {
		if (encodedCloudInfo.equals("SKC") || encodedCloudInfo.equals("CLR")) {
			this.type = "Sky Clear";
			this.altitude = "";
		} else {
			String altitude = new StringBuilder(encodedCloudInfo.substring(3, 6)).reverse().toString();
			this.type = type;
			this.altitude = altitude;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
}