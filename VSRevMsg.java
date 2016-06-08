package vsue.rpc;

import java.io.Serializable;

public class VSRevMsg implements Serializable{

	private Object result;
	private Throwable fehler;
	//private VSRemoteReference remoteReference;
	
	public VSRevMsg(Object result,Throwable fehler){//,VSRemoteReference remoteReference
		
		this.result=result;
		this.fehler = fehler;
		//this.remoteReference = remoteReference;
	}
	
	public VSRevMsg(VSRevMsg revMsg){
		
		//VSRevMsg(revMsg.fehler,revMsg.remoteReferenz);
		
		this.result=revMsg.result;
		this.fehler=revMsg.fehler;
		//this.remoteReference=revMsg.remoteReference;
	}
	
	public Throwable getFehler(){
		return fehler;
	}
	
//	public VSRemoteReference getRemoteReference(){
//		return remoteReference;
//	}
	
	public Object getResult(){
		return result;
	}
}
