package vsue.replica;

import java.io.Serializable;
import java.rmi.Remote;


@SuppressWarnings("serial")
public class VSKeyValueRequest implements Serializable {

	/*
	 * TODO: Implement request
	 */
	private String value;
	private String key;
	private int request;
	VSKeyValueReplyHandler Sc ;
	
	public VSKeyValueRequest (String key,VSKeyValueReplyHandler Sc,String value){
		this.key = key;
		this.value = value;
		this.Sc = Sc;
	}
	
	public VSKeyValueRequest (String key,VSKeyValueReplyHandler Sc,int request){
		this.key = key;
		this.request = request;
		this.Sc = Sc;
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
	public VSKeyValueReplyHandler getClientStub(){
		return this.Sc;
	}
	
	public void setvalue(String value){
		this.value = value;
	}
	
	public void setkey(String key){
		this.key = key;
	}
}
