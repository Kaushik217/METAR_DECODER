package com.metar.decoder.Model;

public class WindInfo {
	private String direction;
	private String speed;
	private String gusts;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getGusts() {
		return gusts;
	}

	public void setGusts(String gusts) {
		this.gusts = gusts;
	}

	public String decodeWindVariability(String encodedWindVariability, int condition) {
		try {
			if (condition == 2) {
				return "Wind direction is variable at " + encodedWindVariability.substring(3, 5) + " knots";
			} else {
				return "Wind direction is varying between " + encodedWindVariability.substring(0, 3) + " and " + encodedWindVariability.substring(4, 7) + " knots";
			}
		} catch (Exception e) {
			System.out.println("An Exception occurred in decodeWindVariability method: " + e.getMessage());
			return "";
		}
	}
}