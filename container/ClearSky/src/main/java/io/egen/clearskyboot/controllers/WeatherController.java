package io.egen.clearskyboot.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.egen.clearskyboot.constants.HTTPStatus;
import io.egen.clearskyboot.constants.Tags;
import io.egen.clearskyboot.constants.URI;
import io.egen.clearskyboot.entities.Reading;
import io.egen.clearskyboot.exceptions.InternalServerError;
import io.egen.clearskyboot.services.WeatherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = URI.READINGS)
@Api(tags = Tags.READINGS)
public class WeatherController {
	
	private Logger logger = LoggerFactory.getLogger(WeatherController.class);
	
	@Autowired
	private WeatherService weatherService;
	
	/**
	 * Description: Get all readings
	 * @return List<Reading>
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = Tags.FIND_ALL_READINGS, notes = Tags.FIND_ALL_READINGS_DESC)
	@ApiResponses(value = { 
							@ApiResponse(code = HTTPStatus.SUCCESS_OK_CODE, message = HTTPStatus.SUCCESS_OK_MSG),
							@ApiResponse(code = HTTPStatus.SERVER_ERR_INTERNAL_CODE, message = HTTPStatus.SERVER_ERR_INTERNAL_MSG) 
						  })
	public List<Reading> findAll() {
		return weatherService.findAll();
	}
	
	/**
	 * Description: Create a new Reading
	 * @param reading
	 * @return Reading
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = Tags.CREATE_READING, notes = Tags.CREATE_READING_DESC)
	@ApiResponses(value = { 
							@ApiResponse(code = HTTPStatus.SUCCESS_OK_CODE, message = HTTPStatus.SUCCESS_OK_MSG),
							@ApiResponse(code = HTTPStatus.CLIENT_ERR_BAD_REQUEST_CODE, message = HTTPStatus.CLIENT_ERR_BAD_REQUEST_MSG),
							@ApiResponse(code = HTTPStatus.SERVER_ERR_INTERNAL_CODE, message = HTTPStatus.SERVER_ERR_INTERNAL_MSG) 
						  })
	public Reading create(@RequestBody Reading reading) {
		return weatherService.create(reading);
	}
	
	/**
	 * Description: Get list of distinct cities.
	 * @return List<Reading>
	 */
	@RequestMapping(method = RequestMethod.GET, value = URI.REPORTED_CITIES)
	@ApiOperation(value = Tags.FIND_DISTINCT_CITIES, notes = Tags.FIND_DISTINCT_CITIES_DESC)
	@ApiResponses(value = { 
							@ApiResponse(code = HTTPStatus.SUCCESS_OK_CODE, message = HTTPStatus.SUCCESS_OK_MSG),
							@ApiResponse(code = HTTPStatus.SERVER_ERR_INTERNAL_CODE, message = HTTPStatus.SERVER_ERR_INTERNAL_MSG) 
						  })
	public List<String> findDistinctCities() {
		return weatherService.findDistinctCities();
	}
	
	/**
	 * Description: Get the latest weather of a city. 
	 * Optional parameters: Property is used to get specific property of weather like temperature or humidity etc.. 
	 * 						Grain is used to get hourly or daily averaged data.
	 * @param city
	 * @param property (Optional)
	 * @param grain	(Optional)
	 * @return JsonNode
	 */
	@RequestMapping(method = RequestMethod.GET, value = URI.LATEST_WEATHER)
	@ApiOperation(value = Tags.FIND_LATEST_WEATHER, notes = Tags.FIND_LATEST_WEATHER_DESC)
	@ApiResponses(value = { 
							@ApiResponse(code = HTTPStatus.SUCCESS_OK_CODE, message = HTTPStatus.SUCCESS_OK_MSG),
							@ApiResponse(code = HTTPStatus.CLIENT_ERR_BAD_REQUEST_CODE, message = HTTPStatus.CLIENT_ERR_BAD_REQUEST_MSG),
							@ApiResponse(code = HTTPStatus.CLIENT_ERR_NOT_FOUND_CODE, message = HTTPStatus.CLIENT_ERR_NOT_FOUND_MSG),
							@ApiResponse(code = HTTPStatus.SERVER_ERR_INTERNAL_CODE, message = HTTPStatus.SERVER_ERR_INTERNAL_MSG) 
						  })
	public JsonNode findLatestWeather( @RequestParam(value = "city", required = true) String city,
								 @RequestParam(value = "property", required = false) String property,
								 @RequestParam(value = "grain", required = false) String grain) {
		
		// Check for Nullable
		Optional<String> optionalProperty = Optional.ofNullable(property);
		Optional<String> optionalGrain = Optional.ofNullable(grain);
		
		if(optionalProperty.isPresent() && optionalGrain.isPresent()) {
			logger.info("both present");
			return weatherService.findAvgWeatherPropertyByCityAndGrain(city, property, grain);}
		else {
			if (optionalProperty.isPresent()) {
				logger.info("property present");
				return weatherService.findLatestWeatherPropertyByCity(city, property);
			} else if(optionalGrain.isPresent()) {
				logger.info("grain present");
				return weatherService.findAvgWeatherPropertyByCityAndGrain(city, "ALL", grain);
			} else {
				
				Optional<Reading> latestWeather = Optional.ofNullable(weatherService.findLatestWeatherByCity(city));
				if(latestWeather.isPresent()) {
					try {
						Reading reading = latestWeather.get();
						System.out.println(reading.toString());
						return new ObjectMapper().readTree(reading.toString());	
					} catch (Exception e) {
						throw new InternalServerError("Error converting latest weather for city : " + e.getMessage() + " ; to JSON");
					}
				}
				return new ObjectMapper().createObjectNode();
			}
		}
	}
}
