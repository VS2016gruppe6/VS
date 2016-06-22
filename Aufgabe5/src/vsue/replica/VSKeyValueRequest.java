package vsue.replica;

import java.io.Serializable;


public class VSKeyValueRequest implements Serializable {

	private VSKeyValueClient Requesting_Client;
	
	private VSKeyValueOperation Operation;
	
	private String arg1;
	
	private String arg2;
	
	private int threshold;
	
	public VSKeyValueRequest(VSKeyValueClient Requesting_Client,
									VSKeyValueOperation Operation,
									String arg1,
									String arg2,
									int threshold){
		this.Requesting_Client = Requesting_Client;
		this.Operation = Operation;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.threshold = threshold;
	}
	
	public VSKeyValueClient GetRequestingClient(){
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
	
	public int GetThreshold(){
		return this.threshold;
	}
	
	public void SetRequestingClient(VSKeyValueClient Requesting_Client) {
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
	
}
