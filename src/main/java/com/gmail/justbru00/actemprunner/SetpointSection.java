package com.gmail.justbru00.actemprunner;

public class SetpointSection {

	private int outdoorLowTemp = -1;
	private int outdoorHighTemp = -1;
	
	private int runtime = -1;
	private int offtime = -1;
	
	public SetpointSection(int outdoorLowTemp, int outdoorHighTemp, int runtime, int offtime) {
		super();
		this.outdoorLowTemp = outdoorLowTemp;
		this.outdoorHighTemp = outdoorHighTemp;
		this.runtime = runtime;
		this.offtime = offtime;
	}
	
	public int getOutdoorLowTemp() {
		return outdoorLowTemp;
	}
	
	public void setOutdoorLowTemp(int outdoorLowTemp) {
		this.outdoorLowTemp = outdoorLowTemp;
	}
	
	public int getOutdoorHighTemp() {
		return outdoorHighTemp;
	}
	
	public void setOutdoorHighTemp(int outdoorHighTemp) {
		this.outdoorHighTemp = outdoorHighTemp;
	}
	
	public int getRuntime() {
		return runtime;
	}
	
	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}
	
	public int getOfftime() {
		return offtime;
	}
	
	public void setOfftime(int offtime) {
		this.offtime = offtime;
	}
	
	
	
}
