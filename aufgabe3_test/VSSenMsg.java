package vsue.rpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.Remote;




public class VSSenMsg implements Serializable{
	
	private int objectID;
	private String method;
	private Object[] args;
	
	private int requestID;
	private int sequenzNr;
	
	//private Remote auctionService; ///fuer Rueckruf
	
	public VSSenMsg(int objectID,String method,Object[] args,int requestID,int sequenzNr){//Remote auctionService
		
		this.objectID=objectID;
		this.method=method;
		this.args=args;
		this.requestID=requestID;
		this.sequenzNr=sequenzNr;
		
	//	this.auctionService=auctionService;
	}
	
//	public VSSenMsg(VSSenMsg senMsg){
//		
//		//VSSenMsg(senMsg.objectID,senMsg.method,senMsg.args);
//		
//		this.objectID=senMsg.objectID;
//		this.method=senMsg.method;
//		this.args=senMsg.args;
		
	//	this.auctionService=senMsg.auctionService;
	//}
	
	public int getObjectID(){
		return objectID;
	}
	
	public String getMethodName(){
		return method;
	}
	
	public Object[] getParameters(){
		return args;
	}
	
	public int getRequestID(){
		return requestID;
	}
	
	public int getSequenzNr(){
		return sequenzNr;
	}
	
//	public static void setSenMsg(int objectID,String method,Object[] args){//,Remote auctoniService VSSenMsg senMsg
//		senMsg.objectID=objectID;
//		senMsg.method=method;
//		senMsg.args=args;
//		
	//	senMsg.auctionService=auctoniService;
		
}
