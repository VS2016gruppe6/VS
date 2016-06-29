package vsue.replica;

import java.io.Serializable;


public class VSKeyValueReply implements Serializable {

	private VSKeyValueRequestHandler Replying_Replica;
	
	private VSKeyValueOperation Operation;
	
	private String Value;
	
	private Long UpdatingTime;
	private Long ReplyID;
	
	public VSKeyValueReply(VSKeyValueRequestHandler Replying_Replica,
							VSKeyValueOperation Operation,
							String Value,
							Long UpdatingTime,
							Long ID){
		
		this.Replying_Replica = Replying_Replica;
		this.Operation = Operation;
		this.Value = Value;
		this.UpdatingTime = UpdatingTime;
		this.ReplyID = ID;
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
	
	public Long GetReplyID(){
		return this.ReplyID;
	}
	
}
