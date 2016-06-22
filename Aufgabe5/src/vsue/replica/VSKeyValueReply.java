package vsue.replica;

import java.io.Serializable;


public class VSKeyValueReply implements Serializable {

	/*
	 * TODO: Implement reply
	 */
	private long time;
	private String value;
	
	public VSKeyValueReply(long time, String value){
		this.time = time;
		this.value = value;
	}
	
	public VSKeyValueReply(String value){
		this.value = value;
	}
	
	public long gettime(){
		return this.time;
	}
	
	public String getvalue(){
		return this.value;
	}
	
}
