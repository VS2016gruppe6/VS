package vsue.distlock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jgroups.Global;




public class timestamp  implements Comparable<timestamp> {
	private Integer counter;
	private Long processID;
	
	public timestamp(int stamp){
		this.counter = stamp;
		processID = Thread.currentThread().getId();
	}
	
	public void setCounter(Integer counter) {
		this.counter = counter;
	}
	
	public Integer getCounter(){
		return this.counter;
	}
	
	public Long getProcessID() {
		return processID;
	}

	public void setProcessID(Long processID) {
		this.processID = processID;
	}
	
	public int getsize() {
		// TODO Auto-generated method stub
		return Global.INT_SIZE + Global.LONG_SIZE;
	}
	
	@Override
	public int compareTo(timestamp o) {
		// TODO Auto-generated method stub
		if(this.counter < o.getCounter()){
			return -1;
		}else if(this.counter > o.getCounter()){
			return 1;
		}else if(this.processID < o.processID){
			return -1;
		}else if(this.processID > o.processID){
			return 1;
		}else{
			return 0;
		}
		
	}
	
	public void readFrom(DataInputStream in) throws IOException {
			this.counter = in.readInt();
			this.processID = in.readLong();
	}
	
	public void writeTo(DataOutputStream out) throws IOException{
			out.writeInt(this.counter);
			out.writeLong(this.processID);
	}

	


}
