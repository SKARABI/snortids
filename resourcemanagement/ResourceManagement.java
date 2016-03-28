package net.floodlightcontroller.fyp.resourcemanagement;

import net.floodlightcontroller.fyp.request.RequestManager;

public class ResourceManagement {

   public static Double minUV, maxUV, minVU, maxVU, capacityU, capacityV , trustU, trustV ,
   alpha, alphaDashUV, alphaDashVU, deltaUV, deltaVU, epsilonUV, epsilonVU , pUV , 
   pVU , prevpUV, neighbours, prevpVU, tUV, tVU, sUV, sVU;   
	
   public Double calAlphaDash(Double min, Double max){
	   return alpha / (max - min);
   }
   
   public Double calDelta(Double alphaDash) {
	   return alphaDash/Math.log(1+alpha);
   }
   
   public Double calP(Double s , Double t, Double pPrev){
	   
	   return s + ( t * pPrev);
   }
   
   public Double calS(Double min, Double max) {
	   
	   return ((1 + 1.0/alpha) * min) - (max/ alpha) ;
   }
   
   public Double calT(Double epsilon, Double trust){
	   
	   return trust/(epsilon * (Math.log(1+alpha)));
   }
   
   public Double calEpsilon(Double delta, Double trust, Double prevPn , Double prevPd, 
		   Double min , Double alphaDash){
	   Double n , d ;
	   
	   n = delta * trust * prevPn;
	   d = 1+((alphaDash * prevPd)-alphaDash*min);
	   
	   return n/d;
   }
   
 
   public void init(){
	   
		//fix constants min max resource , capacity, trust, alpha , other controllers min max
		//calculate alphaDashUV , DeltaUV ,  
	
	   minUV = 1.0;
	   maxUV = 20.0;
	   
	   minVU = 1.0;
	   maxVU = 30.0;
	   
	   capacityU = 50.0;
	   capacityV = 40.0;
	   
	   trustU = 0.6;
	   trustV = 1.0;
	   alpha = 100.0;
	   
	   neighbours = 1.0;
	   
	   pUV = Math.min(capacityU / neighbours , maxUV);
	   pVU = Math.min(capacityV / neighbours, maxVU);
	   
	   alphaDashUV = calAlphaDash(minVU , maxVU);
	   alphaDashVU = calAlphaDash(minUV, maxUV);
	   
	   deltaUV = calDelta(alphaDashUV);
	   deltaVU = calDelta(alphaDashVU);
	   
	   
	   sUV = calS(minUV, maxUV);
	   sVU = calS(minVU, maxVU);
	   
	}
   
   	public void routine(){
   		//for each time slice 
   		prevpUV = pUV;
   		prevpVU = pVU;
   		
   	    epsilonUV = calEpsilon(deltaUV, trustV, RequestManager.actualPVU, prevpUV, minVU, alphaDashUV);
 	    epsilonVU = calEpsilon(deltaVU, trustU, prevpUV, prevpVU, minUV, alphaDashVU);
 	   
   		
 	    tUV = calT(epsilonUV, trustV);
 	    tVU = calT(epsilonVU, trustU);
 	   
 	   
   		pUV = calP(sUV, tUV,  RequestManager.actualPVU);
   		pVU = calP(sVU, tVU, prevpUV);
   		
   	}
}