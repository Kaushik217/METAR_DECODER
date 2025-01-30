package com.metar.decoder.ServiceUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.metar.decoder.Model.CloudInfo;
import com.metar.decoder.Model.TemperatureInfo;
import com.metar.decoder.Model.Weather;
import com.metar.decoder.Model.WeatherData;
import com.metar.decoder.Model.WindInfo;

public class DecodedString {

	public List<WeatherData> DecodedForecasts(List<String> encodedForecasts) {

		List<WeatherData> weatherDataList = new ArrayList<>();

		for (String encodedForecast : encodedForecasts) {
			WeatherData weatherData = decodeForecast(encodedForecast);
			weatherDataList.add(weatherData);
		}
		return weatherDataList;
	}

	private WeatherData decodeForecast(String encodedForecast) {
		WeatherData weatherData = new WeatherData();
		weatherData.setClouds(new ArrayList<>());
		WindInfo wind = new WindInfo();

		try {
			String[] words = encodedForecast.split(" ");

			HashMap<String, String> stationMap = new HashMap<>();
			stationMap.put("PAAQ", "Palmer Municipal");
			stationMap.put("KCLK", "Clinton Regional");
			stationMap.put("KBLV", "KBLV");

			weatherData.decodeStation(words[0], stationMap)
					.decodeDateTime(words[1])
					.decodeType(words[2])
					.decodeWind(words[3]);

			// Weather weather = new Weather();

			// Creating object of TemperaturInfo
			weatherData.setTemperatureAndDewpoint(new TemperatureInfo());
			weatherData.setWeather(new Weather());

			// Other Weather Parameters
			for (int i = 4; i < words.length; i++) {
				// Regular Expression patterns and corresponding functions
				Map<Pattern, Consumer<String>> regexFunctions = new LinkedHashMap<>() {
					{

						put(Pattern.compile("\\d{3}V\\d{3}"),
								str -> weatherData.setWindVariability(wind.decodeWindVariability(str, 1)));
						put(Pattern.compile("VRB\\d{2}KT"),
								str -> weatherData.setWindVariability(wind.decodeWindVariability(str, 2)));
						put(Pattern.compile("\\d{0,2}/\\d{1,2}SM|\\d+SM"),
								str -> weatherData.setVisibility(weatherData.decodeVisibility(str)));
						put(Pattern.compile("R\\d{1,3}[L]/\\d{1,4}FT"),
								str -> weatherData.setRunwayVisualRange(weatherData.decodeRunwayVisualRange(1, str)));
						put(Pattern.compile("R\\d+[L]/\\d+"),
								str -> weatherData.setRunwayVisualRange(weatherData.decodeRunwayVisualRange(2, str)));
						put(Pattern.compile(
								"^([+-]?)(VC|)(BC|BL|DR|FZ|MI|PR|SH|TS|)(DZ|GR|GS|IC|PL|RA|SG|SN|UP|)(BR|DU|FG|FU|HZ|PY|SA|VA|)(DS|FC|PO|SQ|SS|)$"),
								str -> weatherData.getWeather().decodeWeather(str));
						put(Pattern.compile("SKC|CLR"), str -> weatherData.getClouds().add(new CloudInfo(str, "SKC")));
						put(Pattern.compile("FEW\\d{3}"),
								str -> weatherData.getClouds().add(new CloudInfo(str, "FEW")));
						put(Pattern.compile("SCT\\d{3}"),
								str -> weatherData.getClouds().add(new CloudInfo(str, "Scattered")));
						put(Pattern.compile("BKN\\d{3}"),
								str -> weatherData.getClouds().add(new CloudInfo(str, "Broken")));
						put(Pattern.compile("OVC\\d{3}"),
								str -> weatherData.getClouds().add(new CloudInfo(str, "Overcast")));
						put(Pattern.compile("A\\d{4}"),
								str -> weatherData.setAltimeterSetting(weatherData.decodeAltimeterSetting(str)));
						put(Pattern.compile("M\\d{2}/M\\d{2}"),
								str -> weatherData.getTemperatureAndDewpoint().decodeTemperatureAndDewpoint(str, 3));
						put(Pattern.compile("M\\d{2}/\\d{2}"),
								str -> weatherData.getTemperatureAndDewpoint().decodeTemperatureAndDewpoint(str, 4));
						put(Pattern.compile("\\d{2}/M\\d{2}"),
								str -> weatherData.getTemperatureAndDewpoint().decodeTemperatureAndDewpoint(str, 1));
						put(Pattern.compile("\\d{2}/\\d{2}"),
								str -> weatherData.getTemperatureAndDewpoint().decodeTemperatureAndDewpoint(str, 2));
						put(Pattern.compile("RMK"), str -> weatherData.setRemarks("Remarks:"));
						put(Pattern.compile("^A02$"), str -> weatherData.setRemarks(
								"Reported by automated observation equipment that CAN distinguish between rain and snow"));
						put(Pattern.compile("^A01$"), str -> weatherData.setRemarks(
								"Reported by automated observation equipment that CANNOT distinguish between rain and snow"));
						put(Pattern.compile("SLP\\d{3}"), str -> weatherData.setRemarks("Sea Level Pressure is 10"
								+ str.substring(3, 5) + "." + str.substring(5, 6) + " millibars"));
					}
				};

				// Check if the current word matches any regular expression pattern
				for (Map.Entry<Pattern, Consumer<String>> regexFunction : regexFunctions.entrySet()) {
					if (regexFunction.getKey().matcher(words[i]).matches()) {
						regexFunction.getValue().accept(words[i]);
						break;
					}
				}
			}

		} catch (Exception e) {
			System.out.println("An Exception occurred while decoding: " + e.getMessage());
		}
		return weatherData;
	}
}