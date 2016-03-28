package net.floodlightcontroller.fyp.idslistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import net.floodlightcontroller.fyp.request.RequestManager;
import net.floodlightcontroller.fyp.resourcemanagement.ResourceManagement;

public class SnortResource extends ServerResource {
	@Get("json")
	public ArrayList<String> check(String fmJson) {
		ArrayList<String> response = new ArrayList<>();
		response.add("1");
		response.add("2");
		response.add("3");
		return response;
	}
	@Post
	public String post(String json) {
		RequestManager.otherRequestCount++;
		System.out.println("Testing Snort$$$"+json);
		
		if(!RequestManager.isStarted)
		{
			RequestManager.isStarted = true;
			RequestManager requestManager = new RequestManager();
			requestManager.start();
		}
		
		while(ResourceManagement.pUV==null){
			System.out.println("waiting");
		}
		
		if(RequestManager.otherRequestCount.doubleValue() < ResourceManagement.pUV){
		// check for the the attack in given file
			
			
			
			RequestManager.otherResponseCount++;
			return "unknown";
		}
		else{
			return "noshare";
		}
	}
}
