package net.floodlightcontroller.fyp.request;

import net.floodlightcontroller.fyp.resourcemanagement.ResourceManagement;

public class RequestManager extends Thread{

	public static Double actualPVU = 0.0;
	
	public static Integer requestCount = 0;
	public static Integer responseCount = 0;
	
	public static Integer otherRequestCount = 0;
	public static Integer otherResponseCount = 0;
	
	
	public static boolean isStarted = false;
	ResourceManagement r = new ResourceManagement();
	@Override
	public void run() {
		System.out.println("starting request manager");
		r.init();
		actualPVU = ResourceManagement.pVU;
		while(true){
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			actualPVU = responseCount.doubleValue();
			r.routine();
			
			requestCount=0;
			responseCount=0;
			
			otherRequestCount = 0;
			otherResponseCount = 0;
		}
	}

	
}
