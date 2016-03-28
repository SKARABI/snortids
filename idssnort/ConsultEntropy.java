package net.floodlightcontroller.fyp.idssnort;

import java.util.Queue;

import net.floodlightcontroller.fyp.http.IdsHttpRequest;
import net.floodlightcontroller.fyp.request.RequestManager;

public class ConsultEntropy {

	public int requestEntropy(Queue<String> srcIps, Queue<String> dstIps ){
		
		if(!RequestManager.isStarted)
		{
			RequestManager.isStarted = true;
			RequestManager requestManager = new RequestManager();
			requestManager.start();
		}
		System.out.println("consulting snort");
		RequestManager.requestCount++;
		RequestManager.responseCount++;
		String json;
		String list1="[";
		String list2="[";
		int list1first = 0;
		int list2first = 0;
		for(String ip : srcIps){
			
			if(list1first == 0 ){
				list1first=1;
			}
			else{
				list1 = list1 + ","; 
			}
				list1 = list1 + "\""+ip+"\"";
		}
		list1 = list1 +"]";
		for(String ip : dstIps){
			
			if(list2first == 0 ){
				list2first=1;
			}
			else{
				list2 = list2 + ","; 
			}
				list2 = list2 + "\""+ip+"\"";
		}
		
		list2 = list2 +"]";
		json = "{\"window\":80,\"source-ips\":"+list1+",\"dest-ips\":"+list2+"}";
		IdsHttpRequest request = new IdsHttpRequest();
		int status = request.postJSONHttpSync("http://localhost:8082/wm/entropyids/consult", json);
		
		if(status  == -1){
			RequestManager.responseCount--;
		}
		return status;
	}
}
