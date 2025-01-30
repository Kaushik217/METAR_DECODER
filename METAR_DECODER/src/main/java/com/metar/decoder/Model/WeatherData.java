package com.metar.decoder.Model;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class WeatherData {
	private String station;
	private String dateTime;
	private String type;
	private WindInfo wind;
	private String windVariability;
	private String visibility;
	private String runwayVisualRange;
	private List<CloudInfo> clouds;
	private TemperatureInfo temperatureAndDewpoint;
	private String altimeterSetting;
	private String remarks;
	private Weather weatherClass;
	private String date;
	private String time;

	public WeatherData() {
		wind = new WindInfo();
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Weather getWeather() {
		return weatherClass;
	}

	public void setWeather(Weather weatherClass) {
		this.weatherClass = weatherClass;
	}

	public WindInfo getWind() {
		return wind;
	}

	public void setWind(WindInfo wind) {
		this.wind = wind;
	}

	public String getWindVariability() {
		return windVariability;
	}

	public void setWindVariability(String windVariability) {
		this.windVariability = windVariability;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getRunwayVisualRange() {
		return runwayVisualRange;
	}

	public void setRunwayVisualRange(String runwayVisualRange) {
		this.runwayVisualRange = runwayVisualRange;
	}

	public List<CloudInfo> getClouds() {
		return clouds;
	}

	public void setClouds(List<CloudInfo> clouds) {
		this.clouds = clouds;
	}

	public TemperatureInfo getTemperatureAndDewpoint() {
		return temperatureAndDewpoint;
	}

	public void setTemperatureAndDewpoint(TemperatureInfo temperatureAndDewpoint) {
		this.temperatureAndDewpoint = temperatureAndDewpoint;
	}

	public String getAltimeterSetting() {
		return altimeterSetting;
	}

	public void setAltimeterSetting(String altimeterSetting) {
		this.altimeterSetting = altimeterSetting;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public WeatherData decodeStation(String stationCode, HashMap<String, String> stationMap) {
		if (stationMap.containsKey(stationCode)) {
			station = stationMap.get(stationCode);
		} else {
			station = stationCode;
		}
		return this;
	}

	public WeatherData decodeDateTime(String dateTime) {
		this.dateTime = dateTime.substring(0, 2) + " of the month and " + dateTime.substring(2, 6) + " UTC";
		this.date = dateTime.substring(0, 2);
		this.time = dateTime.substring(2, 6);
		return this;
	}

	public WeatherData decodeType(String type) {
		if (Pattern.matches("^COR", type)) {
			this.type = "Corrected Observation";
		} else if (type.equals("AUTO")) {
			this.type = "Automated Observation";
		}
		return this;
	}

	public WeatherData decodeWind(String windInfo) {
		if (windInfo.equals("000000KT")) {
			wind = new WindInfo();
			wind.setSpeed("Calm Wind");
		} else {
			String windDirection = windInfo.substring(0, 3);
			String windSpeed = windInfo.substring(3, 5);
			String gusts = windInfo.length() > 5 && windInfo.charAt(5) == 'G' ? windInfo.substring(6, 8) : null;
			wind = new WindInfo();
			wind.setDirection(windDirection);
			wind.setSpeed(windSpeed);
			wind.setGusts(gusts);
		}
		return this;
	}

	public String decodeVisibility(String encodedVisibility)
	{
		try {
			return encodedVisibility.substring(0, encodedVisibility.length() - 2) + " Status Miles";
		} catch (Exception ex) {
			System.out.println("An error occurred in decodeVisibility method: " + ex.getMessage());
			return "";
		}
	}

	public String decodeRunwayVisualRange(int condition, String encodedRunwayVisualRange) {
		try {
			if (condition == 1) {
				return "Runway Visual Range: Runway " + encodedRunwayVisualRange.substring(1, 3) + " Left is " + encodedRunwayVisualRange.substring(5, 9) + " ft.";
			} else {
				return "Runway Visual Range: " + encodedRunwayVisualRange.substring(1, 3) + " Left is " + encodedRunwayVisualRange.substring(5, 9) + " meters.";
			}
		} catch (Exception ex) {
			System.out.println("An error occurred in decodeRunwayVisualRange method: " + ex.getMessage());
			return "";
		}
	}

	public String decodeAltimeterSetting(String encodedAltimeterSetting) {
		try {
			return encodedAltimeterSetting.substring(1, 3) + "." + encodedAltimeterSetting.substring(3, 5) + " inches of mercury";
		} catch (Exception ex) {
			System.out.println("An error occurred in decodeAltimeterSetting method: " + ex.getMessage());
			return "";
		}
	}
}