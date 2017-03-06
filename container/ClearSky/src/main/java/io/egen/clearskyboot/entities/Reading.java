package io.egen.clearskyboot.entities;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Reading {
	
	@Id
	private String readingId;
	private String city;
	private String description;
	private int humidity;
	private int pressure;
	private int temperature;
	private double windSpeed;
	private int windDegree;
	private Timestamp timestamp;
	
	public Reading() {
		this.readingId = UUID.randomUUID().toString();
	}
	
	public String getReadingId() {
		return readingId;
	}
	public void setReadingId(String readingId) {
		this.readingId = readingId;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}
	public int getPressure() {
		return pressure;
	}
	public void setPressure(int pressure) {
		this.pressure = pressure;
	}
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public double getWindSpeed() {
		return windSpeed;
	}
	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}
	public int getWindDegree() {
		return windDegree;
	}
	public void setWindDegree(int windDegree) {
		this.windDegree = windDegree;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return String.format("{ \"readingId\":\"%s\", \"city\":\"%s\", \"description\":\"%s\", \"humidity\":%d, \"temperature\":%d, \"pressure\":%d, \"windSpeed\":%f, \"windDegree\":%d }", 
				readingId, city, description, humidity, temperature, pressure, windSpeed, windDegree);
	}
	
	@JsonProperty("wind")
	private void unpackNameFromNestedObject(Map<String, String> wind) {
	    setWindSpeed(Double.parseDouble(wind.get("speed")));
	    setWindDegree(Integer.parseInt(wind.get("degree")));
	}
}
