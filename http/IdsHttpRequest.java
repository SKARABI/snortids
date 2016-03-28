package net.floodlightcontroller.fyp.http;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

public class IdsHttpRequest {

	public void postJSONHttp(String url, String json){
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		try {
		    // Start the client
		    httpclient.start();

		    final CountDownLatch latch1 = new CountDownLatch(1);
		 
		    final HttpPost request2 = new HttpPost(url);
		    request2.addHeader("Content-Type", "application/json");
		    HttpEntity entity = new StringEntity(json,ContentType.create("application/json", Consts.UTF_8));
		    request2.setEntity(entity);
		    FutureCallback<HttpResponse> callback=  new FutureCallback<HttpResponse>() {

		        public void completed(final HttpResponse response2) {
		            latch1.countDown();
		            System.out.println(request2.getRequestLine() + "->" + response2.getStatusLine());
		            
		            try {
		            
		            	
		            	//InputStream inputStream = response2.getEntity().getContent();
		            	String responseString = EntityUtils.toString(response2.getEntity(), "UTF-8");
		            	System.out.println(responseString);
				
		            
		            
		            } catch (UnsupportedOperationException | IOException e) {
						
						e.printStackTrace();
					}
		        }

		        public void failed(final Exception ex) {
		            latch1.countDown();
		            System.out.println(request2.getRequestLine() + "->" + ex);
		        }

		        public void cancelled() {
		            latch1.countDown();
		            System.out.println(request2.getRequestLine() + " cancelled");
		        }

		    };
		    httpclient.execute(request2,callback);
		    latch1.await();
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public int postJSONHttpSync(String url, String json) {
		int status = 1;
		
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		try {
		    // Start the client
		    httpclient.start();
		    
		    final HttpPost request2 = new HttpPost(url);
		    request2.addHeader("Content-Type", "application/json");
		    HttpEntity entity = new StringEntity(json,ContentType.create("application/json", Consts.UTF_8));
		    request2.setEntity(entity);
		  
		    Future<HttpResponse> future = httpclient.execute(request2, null);
		    // and wait until a response is received
		    
		    HttpResponse response1 = future.get();
		    String responseString = EntityUtils.toString(response1.getEntity(), "UTF-8");
        	System.out.println(responseString);
	
        	if(responseString.equals("attack")){
        		status = 0;
        		
        	}
        	else if(responseString.equals("noshare")){
        		status = -1;
        	}
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return status;
	}
}
