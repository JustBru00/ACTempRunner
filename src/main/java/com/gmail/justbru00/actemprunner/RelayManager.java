package com.gmail.justbru00.actemprunner;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.pi4j.io.gpio.RaspiPin;

public class RelayManager {
	
	private static ArrayList<SetpointSection> setpoints = new ArrayList<SetpointSection>();
	private static SimplePin airCondRelay = new SimplePin(RaspiPin.GPIO_07, "Air Conditioning Relay" , true);
	
	/**
	 * The current outdoor air temp as reported by OpenWeatherAPI
	 * This value will be -1 if we failed to get the outdoor temp.
	 */
	private static int outdoorAirTemp = -1;
	
	private static Instant startTime = Instant.now();
	
	private static int currentRuntimeSetting = -1;
	private static int currentOfftimeSetting = -1;
	private static boolean runtimeComplete = false;
	
	private static SetpointSection lowestSetpoint = null;
	
	private static Instant lastWeatherUpdate = null;
	
	/**
	 * Checks the current setpoints and updates the relay.
	 */
	public static void updateRelayStatus() {	
		// Make sure we have a setpoint value.
		if (currentRuntimeSetting == -1) {
			// Figure out next setpoint
			System.out.println("Calculating next setpoint...");
			if (outdoorAirTemp == -1) {
				currentRuntimeSetting = lowestSetpoint.getRuntime();
				currentOfftimeSetting = lowestSetpoint.getOfftime();
				runtimeComplete = false;
				System.out.println("Reverting to lowest setpoint because of no air temp value");
			} else {
				// Find the setpoint section for the current temperature.
				SetpointSection match = null;
				for (SetpointSection ss : setpoints) {
					if (outdoorAirTemp >= ss.getOutdoorLowTemp() && outdoorAirTemp <= ss.getOutdoorHighTemp()) {
						match = ss;
					}
				}
				currentRuntimeSetting = match.getRuntime();
				currentOfftimeSetting = match.getOfftime();
				runtimeComplete = false;
			}
		}
		
		// Figure out the relay state
		if (runtimeComplete) {
			// Offtime Counting
			if ((Duration.between(startTime, Instant.now()).getSeconds() / 60) > currentOfftimeSetting) {
				// Done with the offtime
				System.out.println("Offtime Complete! Off for " + Duration.between(startTime, Instant.now()).getSeconds() / 60 + " minutes(s)");
				airCondRelay.setState(SimplePinState.OFF);
				currentRuntimeSetting = -1;
			} else {
				// Not done this section
				System.out.println("Offtime in progress... " + Duration.between(startTime, Instant.now()).getSeconds() / 60 + " minute(s) remain.");
				airCondRelay.setState(SimplePinState.OFF);
			}
		} else {
			// Runtime counting
			if ((Duration.between(startTime, Instant.now()).getSeconds() / 60) > currentRuntimeSetting) {
				// Done with the runtime
				System.out.println("Runtime Complete! Ran for " + Duration.between(startTime, Instant.now()).getSeconds() / 60 + " minutes(s)");
				airCondRelay.setState(SimplePinState.OFF);
				runtimeComplete = true;
			} else {
				// Not done this section
				System.out.println("Runtime in progress... " + Duration.between(startTime, Instant.now()).getSeconds() / 60 + " minute(s) remain.");
				airCondRelay.setState(SimplePinState.ON);
			}
		}
		
		
		
	}
	
	public static void init() {
		airCondRelay.setState(SimplePinState.OFF);
		
		// Read the config and fill in the setpoints
		// TODO Actually read and save a config file.
		SetpointSection lowTemp = new SetpointSection(-30, 49, 0, 10);
		SetpointSection medTemp = new SetpointSection(50, 69, 10, 30);
		SetpointSection highTemp = new SetpointSection(70, 89, 20, 10);
		SetpointSection highestTemp = new SetpointSection(90, 150, 20, 5);
		
		setpoints.add(lowTemp);
		setpoints.add(medTemp);
		setpoints.add(highTemp);
		setpoints.add(highestTemp);
		
		SetpointSection lowest = new SetpointSection(200, 200, 0, 0);
		for (SetpointSection ss : setpoints) {
			if (ss.getOutdoorLowTemp() < lowest.getOutdoorLowTemp()) {
				lowest = ss;
			}
		}
	}
	
	public static void updateOutdoorTemp() {
		// Get temperature as kelvin from api
		// main.temp
		
		if ((Duration.between(lastWeatherUpdate, Instant.now()).getSeconds() / 60) > 12) {
			HttpResponse<JsonNode> json;
			BigDecimal big = new BigDecimal(-100);
			try {
				json = Unirest.get("https://api.openweathermap.org/data/2.5/weather?zip=17543&APPID=a880132d2e4609e673bee2b90972c170").asJson();
				 big = json.getBody().getObject().getJSONObject("main").getBigDecimal("temp");
			} catch (UnirestException e) {
				outdoorAirTemp = -1;
				e.printStackTrace();
			}
			
			if (big != new BigDecimal(-100)) {
				outdoorAirTemp = getTempInDegreesFarenheit(big);
				System.out.println("Outdoor Air Temp is now: " + outdoorAirTemp);
			}
		}
		
	}
	
	/**
	 * 
	 * @param tempInKelvin
	 * @return temp in degrees Farenheit
	 */
	public static Integer getTempInDegreesFarenheit(BigDecimal tempInKelvin) {
		Integer tempInDegFar = 999;
		final double convFactor = 9 / 5;
		int tempInCent = getTempInDegreesCent(tempInKelvin);
		if (tempInCent < 900) {
			tempInDegFar = (int) ((tempInCent * convFactor) + 32);
		}

		return tempInDegFar;
	}
	
	/**
	 * 
	 * @param tempInKelvin
	 * @return temp in degrees Cent
	 */
	public static Integer getTempInDegreesCent(BigDecimal tempInKelvin) {
		Integer degreesInCent = 999;
		final double zeroInKelvin = -273.15;
		if (tempInKelvin == null) {
			return degreesInCent;
		} else {
			degreesInCent = (int) (tempInKelvin.doubleValue() + zeroInKelvin);
			return degreesInCent;
		}
	}

}
