package vsue.distlock;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.Global;
import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.stack.IpAddress;
import org.jgroups.stack.Protocol;
import org.jgroups.util.UUID;




public class VSLogicalClockProtocol extends Protocol  {
	
	private volatile AtomicInteger LC_Counter ;
	private volatile Address localAddress ;

	
	public VSLogicalClockProtocol(){
		LC_Counter = new AtomicInteger(0);
	}
	
	
	public static class ClockHeader extends Header {
		public static final short header_id = 1501;
		private timestamp stamp;
		// Headers need a default constructor for instantiation via reflection.
		public ClockHeader(/* Don't add parameters here! */) { }
		
		public ClockHeader(int Counter, Address address){
			stamp = new timestamp(Counter, address) ;
		}
		
		@Override
		public void writeTo(DataOutput out) throws IOException {
		//	stamp.writeTo((DataOutputStream) out);
			out.writeInt(stamp.getCounter());
			try {
				stamp.getAddress().writeTo(out);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void readFrom(DataInput in) throws IOException {

			stamp = new timestamp();
			stamp.setCounter(in.readInt());
			UUID address = new UUID();
			try {
				address.readFrom(in);
				stamp.setAddress(address);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public timestamp getTimestamp() {
			return stamp;
		}
		
		@Override
		public int size() {	
			return stamp.getsize();
		}
	}

	// --- Interface for LamportLockProtocol class ---
	
	public static int getMessageTime(Message m) {
		ClockHeader header = (ClockHeader) m.getHeader(ClockHeader.header_id);
		if(header != null)
			return (int)header.getTimestamp().getCounter();
		else
			return -1;
	}
	
	// --- Protocol implementation ---
	
	@Override
	public Object down(Event evt) {
		if(localAddress == null){
			localAddress = getProtocolStack().getChannel().getAddress();
		}

		switch (evt.getType()) {
		case Event.MSG:
			
			Message m = (Message) evt.getArg();	
			synchronized (LC_Counter){
			ClockHeader header =  new ClockHeader(LC_Counter.getAndIncrement(), localAddress);
			m.putHeader(ClockHeader.header_id, header);
			return down_prot.down(new Event(Event.MSG,m));
			}
		default:	
			return down_prot.down(evt); 
		}
	}
	@Override
	public Object up(Event evt) {
		if(localAddress == null){
			localAddress = getProtocolStack().getChannel().getAddress();
		}

		try {
			switch(evt.getType()){
			case Event.MSG:
				Message m = (Message) evt.getArg();
				ClockHeader CH = (ClockHeader) m.getHeader(ClockHeader.header_id);
				if (CH == null) {
					return up_prot.up(evt);
				}
				synchronized(LC_Counter){
					timestamp localstamp = new timestamp(LC_Counter.get(), localAddress);
					switch(CH.getTimestamp().compareTo(localstamp)) {
					case -1:
						LC_Counter.incrementAndGet();
						break;
					case 1:
						LC_Counter.set(CH.getTimestamp().getCounter()+1);
						break;

					case 0:
						//	System.out.println("Error: Equal Timestamps");
					default :
						break;
					}
				}
			default:
				return up_prot.up(evt);
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void init() {
		ClassConfigurator.add(ClockHeader.header_id, ClockHeader.class);
	}
}
