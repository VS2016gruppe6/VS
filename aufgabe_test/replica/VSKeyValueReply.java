package vsue.replica;

import java.io.Serializable;


@SuppressWarnings("serial")
public class VSKeyValueReply implements Serializable {

	private VSKeyValueRequestHandler Replying_Replica;
	
	private VSKeyValueOperation Operation;
	
	private String Value;
	
	private long getTime;
	
	public VSKeyValueReply(VSKeyValueRequestHandler Replying_Replica,
							VSKeyValueOperation Operation,
							String Value,
							Long getTime){
		
		this.Replying_Replica = Replying_Replica;
		this.Operation = Operation;
		this.Value = Value;
		this.getTime = getTime;
	}
	
	public VSKeyValueRequestHandler GetReplyingReplica(){
		return this.Replying_Replica;
	}
	
	public VSKeyValueOperation GetReplyingOperation(){
		return this.Operation;
	}
	
	public String GetReplyingValue(){
		return this.Value;
	}
	
	public long getTime(){
		return this.getTime;
	}
	
}
