package vsue.distlock;

import org.jgroups.Address;
import org.jgroups.Global;





public class timestamp  implements Comparable<timestamp> {
	private Integer counter;
	private Address address;
	
	public timestamp(int stamp, Address address){
		this.counter = stamp;
		this.address = address;
	}
	
	public timestamp(){
		this.counter = 0;
		this.address = null;
	}
	
	public void setCounter(Integer counter) {
		this.counter = counter;
	}
	
	public Integer getCounter(){
		return this.counter;
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public int getsize() {
		// TODO Auto-generated method stub
		return Global.INT_SIZE + address.size();
	}
	
	@Override
	public int compareTo(timestamp o) {
		// TODO Auto-generated method stub
		if(this.counter < o.getCounter()){
			return -1;
		}else if(this.counter > o.getCounter()){
			return 1;
		}else {
			return this.address.compareTo(o.address);
		}
		
	}
	
//	public void readFrom(DataInputStream in) throws IOException {
//			this.counter = in.readInt();
//			this.processID = in.readLong();
//	}
//	
//	public void writeTo(DataOutputStream out) throws IOException{
//			out.writeInt(this.counter);
//			out.writeLong(this.processID);
//	}

	


}
