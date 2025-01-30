package com.metar.decoder.Model;

public class Weather {
	private String intensity;
	private String proximity;
	private String description;
	private String precipitation;
	private String obscuration;
	private String other;

	public Weather decodeWeather(String encodedWeatherString) {
		Weather weather = this;
		if (encodedWeatherString.startsWith("+")) {
			weather.intensity = "Heavy";
		} else if (encodedWeatherString.startsWith("-")) {
			weather.intensity = "Light";
		} else {
			weather.intensity = "Moderate";
		}

		weather.proximity = encodedWeatherString.contains("VC") ? "In the vicinity" : "On station";

		String[] descriptionCodes = {"BC", "BL", "DR", "FZ", "MI", "PR", "SH", "TS"};
		for (String code : descriptionCodes) {
			if (encodedWeatherString.contains(code)) {
				weather.description = getDescription(code);
				break;
			}
		}

		String[] precipitationCodes = {"DZ", "GR", "GS", "IC", "PL", "RA", " SG", "SN", "UP"};
		for (String code : precipitationCodes) {
			if (encodedWeatherString.contains(code)) {
				weather.precipitation = getPrecipitation(code);
				break;
			}
		}

		String[] obscurationCodes = {"BR", "DU", "FG", "FU", "HZ", "PY", "SA", "VA"};
		for (String code : obscurationCodes) {
			if (encodedWeatherString.contains(code)) {
				weather.obscuration = getObscuration(code);
				break;
			}
		}

		String[] otherCodes = {"DS", "FC", "PO", "SQ", "SS"};
		for (String code : otherCodes) {
			if (encodedWeatherString.contains(code)) {
				weather.other = getOther(code);
				break;
			}
		}
		return weather;
	}

	private String getDescription(String code) {
		switch (code) {
		case "BC":
			return "Patches";
		case "BL":
			return "Blowing";
		case "DR":
			return "Low Drifting";
		case "FZ":
			return "Freezing";
		case "MI":
			return "Shallow";
		case "PR":
			return "Partial (covering part of the sky)";
		case "SH":
			return "Shower(s)";
		case "TS":
			return "Thunderstorm";
		default:
			return null;
		}
	}

	private String getPrecipitation(String code) {
		switch (code) {
		case "DZ":
			return "Drizzle";
		case "GR":
			return "Hail, diam. ≥ 5mm (.25\")";
		case "GS":
			return "Small Hail / Snow Pellets, diam. < 5mm (.25\")";
		case "IC":
			return "Ice Crystals";
		case "PL":
			return "Ice Pellets";
		case "RA":
			return "Rain";
		case "SG":
			return "Snow Grains";
		case "SN":
			return "Snow";
		case "UP":
			return "Unknown Precipitation";
		default:
			return null;
		}
	}

	private String getObscuration(String code) {
		switch (code) {
		case "BR":
			return "Mist, vis. ≥ 5/8SM (or ≥ 1000m)";
		case "DU":
			return "Widespread Dust";
		case "FG":
			return "Fog, vis. < 5/8SM (or 1000m)";
		case "FU":
			return "Smoke";
		case "HZ":
			return "Haze";
		case "PY":
			return "Spray";
		case "SA":
			return "Sand";
		case "VA":
			return "Volcanic Ash";
		default:
			return null;
		}
	}

	private String getOther(String code) {
		switch (code) {
		case "DS":
			return "Dust Storm";
		case "FC":
			return "Funnel cloud(s)";
		case "PO":
			return "Well-developed dust/sand whirls";
		case "SQ":
			return "Squalls";
		case "SS":
			return "Sandstorm";
		default:
			return null;
		}
	}

	// Getters and Setters
	public String getIntensity() {
		return intensity;
	}

	public void setIntensity(String intensity) {
		this.intensity = intensity;
	}

	public String getProximity() {
		return proximity;
	}

	public void setProximity(String proximity) {
		this.proximity = proximity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(String precipitation) {
		this.precipitation = precipitation;
	}

	public String getObscuration() {
		return obscuration;
	}

	public void setObscuration(String obscuration) {
		this.obscuration = obscuration;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
}