package vsue.rpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.Remote;




public class VSSenMsg implements Serializable{
	
	private int objectID;
	private String method;
	private Object[] args;
	private int Seq;
	private int ID;
	
	//private Remote auctionService; ///fuer Rueckruf
	
	public VSSenMsg(int objectID,String method,Object[] args,int ID,int Seq){//Remote auctionService
		
		this.objectID=objectID;
		this.method=method;
		this.args=args;
		this.ID = ID;
		this.Seq = Seq;
		
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
	
	public int getID(){
		return ID;
	}
	
	public int getSeq(){
		return Seq;
	}
	
	public void setSeq(int Seq){
		this.Seq = Seq;
	}
	
//	public static void setSenMsg(int objectID,String method,Object[] args){//,Remote auctoniService VSSenMsg senMsg
//		senMsg.objectID=objectID;
//		senMsg.method=method;
//		senMsg.args=args;
//		
	//	senMsg.auctionService=auctoniService;
		
}
