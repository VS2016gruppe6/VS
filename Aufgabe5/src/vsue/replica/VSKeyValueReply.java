package vsue.replica;

import java.io.Serializable;


public class VSKeyValueReply implements Serializable {

	private VSKeyValueRequestHandler Replying_Replica;
	
	private VSKeyValueOperation Operation;
	
	private String Value;
	
	private Long UpdatingTime;
	
	public VSKeyValueReply(VSKeyValueRequestHandler Replying_Replica,
							VSKeyValueOperation Operation,
							String Value,
							Long UpdatingTime){
		
		this.Replying_Replica = Replying_Replica;
		this.Operation = Operation;
		this.Value = Value;
		this.UpdatingTime = UpdatingTime;
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
	
	public Long GetUpdatingTime(){
		return this.UpdatingTime;
	}
	
}
