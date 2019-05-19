package com.gmail.justbru00.actemprunner;

import java.io.IOException;

import com.mashape.unirest.http.Unirest;

/**
 * ACTempRunner
 * 
 * This is a command line program that toggles a relay based on different cut in and cut out times depending on the outside temperature.
 * 
 * @author Justin Brubaker
 *
 */
public class App {	
	public static void main(String[] args) {
		System.out.println("Starting ACTempRunner " + Reference.VERSION + "...");
		System.out.println("This program uses the PI4J library.");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	            try {	            	
	                Thread.sleep(200);
	                System.out.println("\nReceived shutdown request from system. (CTRL-C)");
	                
	                Reference.RUNNING = false;	
	                RelayManager.shutdown();
	                try {
						Unirest.shutdown();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    });
		
		RelayManager.init();
		RelayManager.updateOutdoorTemp();
		
		while (Reference.RUNNING) {		
			
			RelayManager.updateRelayStatus();
			RelayManager.updateOutdoorTemp();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	}
	
	
	
}
