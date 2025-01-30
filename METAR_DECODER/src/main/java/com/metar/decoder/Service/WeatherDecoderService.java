package com.metar.decoder.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.metar.decoder.Model.WeatherData;
import com.metar.decoder.ServiceUtility.DecodedString;

@Service
public class WeatherDecoderService {
	
	public List<WeatherData> Decode(List<String> encodedForecasts){
		DecodedString decodedString = new DecodedString();
		return decodedString.DecodedForecasts(encodedForecasts);
	}
}
