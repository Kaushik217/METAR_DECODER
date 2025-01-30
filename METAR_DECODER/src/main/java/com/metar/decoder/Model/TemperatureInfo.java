package com.metar.decoder.Model;

public class TemperatureInfo {
	private String air;
	private String dew;

	public TemperatureInfo decodeTemperatureAndDewpoint(String encodedTemperature, int condition) {
		try {
			String temperature, dewpoint;
			if (condition == 1) {
				temperature = encodedTemperature.substring(0, 2);
				dewpoint = "-" + encodedTemperature.substring(4, 6);
			} else if (condition == 2) {
				temperature = encodedTemperature.substring(0, 2);
				dewpoint = encodedTemperature.substring(3, 5);
			} else if (condition == 3) {
				temperature = "-" + encodedTemperature.substring(1, 3);
				dewpoint = "-" + encodedTemperature.substring(5, 7);
			} else {
				temperature = "-" + encodedTemperature.substring(1, 3);
				dewpoint = encodedTemperature.substring(4, 6);
			}
			this.air = temperature;
			this.dew = dewpoint;
			return this;
		} catch (Exception ex) {
			System.out.println("An error occurred in decodeTemperatureAndDewpoint method: " + ex.getMessage());
			return null;
		}
	}

	public String getAir() {
		return air;
	}

	public void setAir(String air) {
		this.air = air;
	}

	public String getDew() {
		return dew;
	}

	public void setDew(String dew) {
		this.dew = dew;
	}
}