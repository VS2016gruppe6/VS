package vsue.replica;

import java.io.Serializable;


@SuppressWarnings("serial")
public class VSKeyValueRequest implements Serializable {

	private VSKeyValueReplyHandler Requesting_Client;
	
	private VSKeyValueOperation Operation;
	
	private String arg1;
	
	private String arg2;
	
	private long getTime;
	
	public VSKeyValueRequest(VSKeyValueReplyHandler Requesting_Client,
									VSKeyValueOperation Operation,
									String arg1,
									String arg2,
									long getTime){
		this.Requesting_Client = Requesting_Client;
		this.Operation = Operation;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.getTime = getTime;
	}
	
	public VSKeyValueReplyHandler GetRequestingClient(){
		return this.Requesting_Client;
	}
	
	public VSKeyValueOperation GetRequestingOperration(){
		return this.Operation;
	}
	
	public String GetOperationArg1(){
		return this.arg1;
	}
	
	public String GerOperationArg2(){
		return this.arg2;
	}
	
	public long getTIme(){
		return this.getTime;
	}
	
	public void SetRequestingClient(VSKeyValueReplyHandler Requesting_Client) {
		this.Requesting_Client = Requesting_Client;
	}
	
	public void SetRequestingOperation(VSKeyValueOperation Operation){
		this.Operation = Operation;
	}
	
	public void SetOperationArgs(String arg1,String arg2){
		this.arg1 = arg1;
		this.arg2 = arg2;		
	}
	
	/*
	 * TODO: Implement request
	 */
	private String value;
	private String key;
	private int request;
	public VSKeyValueRequest (String key, String value){
		this.key = key;
		this.value = value;
	}
	
	public VSKeyValueRequest (String key,int request){
		this.key = key;
		this.request = request;
	}
	
	public String getkey(){
		return this.key;
	}
	
	public String getvalue(){
		return this.value;
	}
	
	public int getrequest(){
		return this.request;
	}
	
	public void setvalue(String value){
		this.value = value;
	}
	
	public void setkey(String key){
		this.key = key;
	}
}
