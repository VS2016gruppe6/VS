package vsue.replica;

import java.io.Serializable;


@SuppressWarnings("serial")
public class VSKeyValueRequest implements Serializable {

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
