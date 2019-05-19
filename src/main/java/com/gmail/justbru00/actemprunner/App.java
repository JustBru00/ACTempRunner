package com.gmail.justbru00.actemprunner;

import com.pi4j.io.gpio.RaspiPin;

/**
 * ACTempRunner
 * 
 * This is a command line program that toggles a relay based on different cut in and cut out times depending on the outside temperature.
 * 
 * @author Justin Brubaker
 *
 */
public class App {
	private static SimplePin airCondRelay = new SimplePin(RaspiPin.GPIO_07, "Air Conditioning Relay" , true);
	
	public static void main(String[] args) {
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	            try {	            	
	                Thread.sleep(200);
	                System.out.println("\nReceived shutdown request from system. (CTRL-C)");
	                
	                Reference.RUNNING = false;		                
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    });
		
		while (Reference.RUNNING) {		
			
			// Toggle the relay
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	}
	
	
	
}
