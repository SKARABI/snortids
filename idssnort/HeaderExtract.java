package net.floodlightcontroller.fyp.idssnort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.fyp.http.IdsHttpRequest;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.ICMP;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntries;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;
import net.floodlightcontroller.staticflowentry.web.StaticFlowEntryPusherResource;
import net.floodlightcontroller.staticflowentry.web.StaticFlowEntryWebRoutable;
import net.floodlightcontroller.storage.IStorageSourceService;


public class HeaderExtract extends ServerResource implements IOFMessageListener, IFloodlightModule {
	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
	protected static Integer packetCount=0,threshold=100,totalCount;
	protected static Double averageSrcIpEntropy,averageDstIpEntropy;
	protected Queue<String> srcIps,dstIps;
	protected HashMap<String, Integer> srcIpMap,dstIpMap;
	public static int sendingcount=0;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return HeaderExtract.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
	    Collection<Class<? extends IFloodlightService>> l =
	            new ArrayList<Class<? extends IFloodlightService>>();
	       // l.add(IFloodlightProviderService.class);
	   // l.add(IRestApiService.class);
	        return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	    macAddresses = new ConcurrentSkipListSet<Long>();
	    logger = LoggerFactory.getLogger(HeaderExtract.class);
	    srcIps = new LinkedList<String>();
	    dstIps = new LinkedList<String>();
	    srcIpMap = new HashMap<>();
	    dstIpMap = new HashMap<>();
	    

	    averageSrcIpEntropy=0.0;
	    averageDstIpEntropy=0.0;
	    totalCount = 0;
	    
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
	    floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	    
	   
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg,
			FloodlightContext cntx) {
		// TODO Auto-generated method stub
		
		totalCount++;
		
		OFPacketIn pIn = (OFPacketIn) msg;
		pIn.getInPort();
		
		int status =0;
        Ethernet eth =
                IFloodlightProviderService.bcStore.get(cntx,
                                            IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        logger.info("PACKET IN MESSAGE" + eth.getEtherType().getValue());
      /*  if(eth.getEtherType() == EthType.ARP){
        	ARP arp = (ARP) eth.getPayload();
        	logger.info(arp.getSenderHardwareAddress().toString());
        }*/
        
        if(eth.getEtherType() == EthType.IPv4){
       //if(eth.getEtherType().getValue() == 2048 ){
    	  
        IPv4 ip = (IPv4) eth.getPayload();
        	IPv4Address srcIp = ip.getSourceAddress();
     		IPv4Address dstIp = ip.getDestinationAddress();
    /*
     		if (ip.getProtocol().equals(IpProtocol.TCP)) {
				TCP tcp = (TCP) ip.getPayload();
				tcp.getSourcePort().toString();
				tcp.getDestinationPort();
			} else if (ip.getProtocol().equals(IpProtocol.UDP)) {
				UDP udp = (UDP) ip.getPayload();
				udp.getSourcePort().toString();
				udp.getDestinationPort().toString();
			} else if (ip.getProtocol().equals(IpProtocol.ICMP)) {
				ICMP icmp = (ICMP) ip.getPayload();
				
			}
     */		
     		
            logger.info("$$$$$$ Source MAC Address: {} Source Ip address: {} $$$$$$",
                    eth.getSourceMACAddress().toString(),
            		srcIp.toString()
            		/*sw.getId().toString()*/);
            logger.info("$$$$$$ Destination MAC Address: {} Destination Ip address: {} $$$$$$",
                    eth.getDestinationMACAddress().toString(),
            		dstIp.toString()
            		/*sw.getId().toString()*/);

             /*OFPacketIn pin = (OFPacketIn) msg;
             OFFactory factory = sw.getOFFactory(); 
             OFMatchV1 match = factory.buildMatchV1().build();
             match.writeTo(Unpooled.copiedBuffer(pin.getP));
             */
             
           
            
                 
                srcIps.add(srcIp.toString());
             	dstIps.add(dstIp.toString());
             	Integer srcIpCount = srcIpMap.get(srcIp.toString());
             	Integer dstIpCount = dstIpMap.get(dstIp.toString());
             	
             	//code for source ip
             	if(srcIpCount != null){
             		srcIpMap.put(srcIp.toString(), srcIpCount+1);
             		
             	}
             	else{
             		srcIpMap.put(srcIp.toString(), 1);
             	}
             	
             	//code for destination ip
             	if(dstIpCount != null ){
             		dstIpMap.put(dstIp.toString(), dstIpCount+1);
             	}
             	else{
             		dstIpMap.put(dstIp.toString(), 1);
             	}
             	System.out.println(packetCount+" "+threshold);
             if(packetCount < threshold)
                packetCount++;
             else{
            	 packetCount = 0;
            	 
            	 ConsultEntropy consultEntropy = new ConsultEntropy();
            	
            	 status = consultEntropy.requestEntropy(srcIps, dstIps);
            	 srcIps.clear();
            	 dstIps.clear();
         		 if(status == 0){
         			String json = "{\"switch\":\""+sw.getId().toString()+"\",\"name\":\"flow-mod-1\", \"cookie\":\"0\", \"priority\":\"32768\", \"in_port\":\""+pIn.getInPort().toString()+"\",\"active\":\"true\"}";
         			System.out.println(json);
         			IdsHttpRequest request = new IdsHttpRequest();
         			request.postJSONHttp("http://localhost:8083/wm/staticflowpusher/json", json);
         			
         		 }
             }
             
       }
       return Command.CONTINUE;
	}
	
}
