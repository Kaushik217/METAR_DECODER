package com.metar.decoder.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.metar.decoder.Model.WeatherData;
import com.metar.decoder.Service.JsonFileManager;
import com.metar.decoder.Service.WeatherDecoderService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

	@Value("${file.path.input}")
	private String inputFilePath;

	@Value("${file.path.output}")
	private String outputFilePath;

	private final JsonFileManager jsonFileManager;
	private final WeatherDecoderService weatherDecoderService;
	@Autowired
	private ObjectMapper objectMapper;

	public WeatherController(JsonFileManager jsonFileManager, WeatherDecoderService weatherDecoderService) {
		super();
		this.jsonFileManager = jsonFileManager;
		this.weatherDecoderService = weatherDecoderService;

	}

	@PostConstruct
	// Clear the output file during application startup
	public void clearOutputFile() {
		try {
			jsonFileManager.clearFile(outputFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to clear output file: " + outputFilePath, e);
		}
	}

	// *********************MULTIPLE ROUTES FOR SINGLE
	// API****************************************************
	// Get all decoded weather data without role-based authorization and
	// authentication
	@GetMapping({ "/decoded", "/Kaushik" })
	public ResponseEntity<?> getAllWeatherData() {
		try {

			String jsonData = jsonFileManager.readJsonDataFromFile(inputFilePath);
			List<String> encodedForecasts = jsonFileManager.deserializeJsonToList(jsonData);
			List<WeatherData> decodedWeatherData = weatherDecoderService.Decode(encodedForecasts);

			// serialize and write the output data into another json file.
			String outputJson = objectMapper.writeValueAsString(decodedWeatherData);
			jsonFileManager.writeJsonDataToFile(outputFilePath, outputJson);

			return ResponseEntity.ok(decodedWeatherData);

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error retrieving weather data.");
		}
	}

	// ********************DYNAMIC
	// ROUTIC*********************************************************************
	// get data by one parameter with authorization and authentication
	@GetMapping("/decoded/{location}")
	public ResponseEntity<?> getDataByLocation(@PathVariable String location, @AuthenticationPrincipal User user) {
		try {

			// check if the user has the "ADMIN" Role
			if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
				// retrieve all weather data
				ResponseEntity<?> response = getAllWeatherData();

				if (response.getBody() instanceof List) {

					@SuppressWarnings("unchecked")
					List<WeatherData> allData = (List<WeatherData>) response.getBody();

					// Filter data based on provided location
					List<WeatherData> weatherDataByLocation = allData.stream()
							.filter(data -> location.equalsIgnoreCase(data.getStation()))
							.collect(Collectors.toList());

					return ResponseEntity.ok(weatherDataByLocation);
				} else {
					return ResponseEntity.internalServerError().body("Failed to fetch all weather data by location.");
				}
			} else {
				// If the user is not an ADMIN, return a 403 Forbidden error
				return ResponseEntity.status(403).body("Access denied. Only admins can access this data.");
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error retrieving weather data by location");
		}

	}

	// get data based on parameter but by not passing the parameter
	@GetMapping("decoded/bynotepassingparameter")
	public ResponseEntity<?> getByParameterByNotPassingParameter() {
		try {
			ResponseEntity<?> response = getAllWeatherData();

			if (response.getBody() instanceof List) {
				@SuppressWarnings("unchecked")
				List<WeatherData> allData = (List<WeatherData>) response.getBody();

				// Filter the data based on the most recent time.
				WeatherData mostRecentTime = null;
				String mostRecentDateTime = "";

				for (WeatherData data : allData) {
					if (mostRecentDateTime.isEmpty() || data.getDateTime().compareTo(mostRecentDateTime) > 0) {
						mostRecentDateTime = data.getDateTime();
						mostRecentTime = data;
					}
				}

				if (mostRecentTime != null) {
					return ResponseEntity.ok(mostRecentTime);
				} else {
					return ResponseEntity.status(404).body("No weather data found.");
				}
			} else {
				return ResponseEntity.internalServerError().body("Failed to fetch weather data by most recent time.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Error retrieving weather data by most recent time");
		}
	}

	// get data by two parameters.
	@GetMapping("/decoded/bytwoparameter")
	public ResponseEntity<?> getDataByTwoParameter(@RequestParam("location") String location,
			@RequestParam("type") String type) {
		try {
			ResponseEntity<?> response = getAllWeatherData();

			if (response.getBody() instanceof List) {
				@SuppressWarnings("unchecked")
				List<WeatherData> allData = (List<WeatherData>) response.getBody();

				List<WeatherData> getResultByTwoParameters = allData.stream()
						.filter(data -> location.equalsIgnoreCase(data.getStation())
								&& type.equalsIgnoreCase(data.getType()))
						.collect(Collectors.toList());

				if (getResultByTwoParameters != null) {
					return ResponseEntity.ok(getResultByTwoParameters);
				} else {
					return ResponseEntity.status(404).body("Failed to fetch weather data by two parameters.");
				}
			} else {
				return ResponseEntity.internalServerError().body("Failed to fetch weather data by two parameters.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Error retrieving weather data by two parameters");
		}
	}

	// get the data from header (one is optional and another is mandatory)
	@GetMapping("/decoded/optionalparameter")
	public ResponseEntity<?> optionalParameter(@RequestParam("type") String type,
			@RequestParam(value = "location", required = false) String location) {
		try {
			ResponseEntity<?> response = getAllWeatherData();

			if (response.getBody() instanceof List) {
				@SuppressWarnings("unchecked")
				List<WeatherData> allData = (List<WeatherData>) response.getBody();

				List<WeatherData> getResultByOptionalParameter = allData.stream()
						.filter(data -> type.equalsIgnoreCase(data.getType())
								&& (location == null || location.isEmpty()
										|| location.equalsIgnoreCase(data.getStation())))
						.collect(Collectors.toList());

				if (getResultByOptionalParameter != null) {
					return ResponseEntity.ok(getResultByOptionalParameter);
				} else {
					return ResponseEntity.status(404).body("Failed to fetch weather data by one optional parameter.");
				}
			} else {
				return ResponseEntity.internalServerError()
						.body("Failed to fetch weather data by one optional parameter.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Error retrieving weather data by one optional parameter");
		}
	}

	// get the data from header
	@GetMapping("/decoded/getfromheader")
	public ResponseEntity<?> getDataByParameterFromHeader(@RequestHeader(name = "X-Custom-Header") String location) {
		try {
			ResponseEntity<?> response = getAllWeatherData();

			if (response.getBody() instanceof List) {
				@SuppressWarnings("unchecked")
				List<WeatherData> allData = (List<WeatherData>) response.getBody();

				List<WeatherData> weatherDataByLocation = allData.stream()
						.filter(data -> location.equalsIgnoreCase(data.getStation()))
						.collect(Collectors.toList());

				if (weatherDataByLocation != null) {
					return ResponseEntity.ok(weatherDataByLocation);
				} else {
					return ResponseEntity.status(404)
							.body("Failed to fetch weather data by get the data by passing parameter through header.");
				}
			} else {
				return ResponseEntity.internalServerError()
						.body("Failed to fetch weather data by get the data by passing parameter through header.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.body("Error retrieving weather data by get the data by passing parameter through header");
		}
	}

	// **************************************DYNAMIC RECORDS
	// FILTERING********************************************

	// get the data by adding filters dynamically
	@GetMapping("/decoded/dynamicfilter")
	public ResponseEntity<?> dynamicRecordsFiltering(
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "date", required = false) String date) {
		try {
			ResponseEntity<?> response = getAllWeatherData();

			if (response.getBody() instanceof List) {
				@SuppressWarnings("unchecked")
				List<WeatherData> allData = (List<WeatherData>) response.getBody();

				List<WeatherData> dynamicfilter = allData.stream()
						.filter(data -> (location == null || location.isEmpty()
								|| location.equalsIgnoreCase(data.getStation()))
								&& (type == null || type.isEmpty() || type.equalsIgnoreCase(data.getType()))
								&& (date == null || date.isEmpty() || date.equalsIgnoreCase(data.getDate())))
						.collect(Collectors.toList());

				if (dynamicfilter != null) {
					return ResponseEntity.ok(dynamicfilter);
				} else {
					return ResponseEntity.status(404).body("No weather data found matching the filters.");
				}
			} else {
				return ResponseEntity.internalServerError()
						.body("Error retrieving weather data by get the data by dynamic filtering");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.body("Error retrieving weather data by get the data by dynamic filtering");
		}
	}

	// dynamic records filtering by passing just one parameter.
	// In this method we are going to get the filtered result for multiple
	// parameters by passing one single parameter
	@GetMapping("/decoded/filterbyoneparameter")
	public ResponseEntity<?> getFilterdResultOfMultipleParametersByPassingSingle(
			@RequestParam("filter") String filter) {
		try {
			String location = "";
			String type = "";
			String date = "";

			if (filter != null) {
				String[] Parts = filter.split(",");

				// loop through parts and assign values through parameter
				for (String part : Parts) {
					String trimmedPart = part.trim();
					if (trimmedPart.equalsIgnoreCase("Automated Observation")
							|| trimmedPart.equalsIgnoreCase("Corrected Observation")) {
						type = trimmedPart;
					} else if (trimmedPart.length() == 2 && trimmedPart.chars().allMatch(Character::isDigit)) {
						date = trimmedPart;
					} else {
						location = trimmedPart;
					}
				}
			}

			ResponseEntity<?> response = getAllWeatherData();

			if (response.getBody() instanceof List) {
				@SuppressWarnings("unchecked")
				List<WeatherData> allData = (List<WeatherData>) response.getBody();

				List<WeatherData> dynamicfilter = new ArrayList<>();

				for (WeatherData data : allData) {
					if ((location == null || location.isEmpty() || location.equalsIgnoreCase(data.getStation()))
							&& (type == null || type.isEmpty() || type.equalsIgnoreCase(data.getType()))
							&& (date == null || date.isEmpty() || date.equalsIgnoreCase(data.getDate()))) {
						dynamicfilter.add(data);
					}
				}

				if (dynamicfilter != null) {
					return ResponseEntity.ok(dynamicfilter);
				} else {
					return ResponseEntity.status(404).body("No weather data found matching the filters.");
				}
			} else {
				return ResponseEntity.internalServerError().body(
						"Error retrieving weather data by get Filterd Result Of Multiple Parameters By Passing Single");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(
					"Error retrieving weather data by get Filterd Result Of Multiple Parameters By Passing Single.");
		}
	}
}

