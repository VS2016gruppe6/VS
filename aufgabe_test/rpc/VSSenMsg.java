package vsue.rpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.Remote;




public class VSSenMsg implements Serializable{
	
	private String method;
	private Object[] args;
	private int requestID;
	private int sequenzNr;
	private int ObjectID;
	//private Remote auctionService; ///fuer Rueckruf
	
	public VSSenMsg(int ObjectID,String method,Object[] args,int requestID,int sequenzNr){//Remote auctionService
		
		this.method=method;
		this.args=args;
		this.requestID=requestID;
		this.sequenzNr=sequenzNr;
		this.ObjectID= ObjectID;
	//	this.auctionService=auctionService;
	}
	public int getObjectID(){
		return ObjectID;
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
}
