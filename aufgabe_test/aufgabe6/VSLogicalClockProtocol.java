package vsue.distlock;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgroups.Event;
import org.jgroups.Global;
import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.stack.Protocol;




public class VSLogicalClockProtocol extends Protocol  {
	
	private volatile AtomicInteger LC_Counter ;
	
	public VSLogicalClockProtocol(){
		LC_Counter = new AtomicInteger(0);
	}
	
	
	public static class ClockHeader extends Header {
		public static final short header_id = 1501;
		private timestamp stamp;
		// Headers need a default constructor for instantiation via reflection.
		public ClockHeader(/* Don't add parameters here! */) { }
		
		public ClockHeader(int Counter){
			stamp = new timestamp(Counter) ;
		}
		
		@Override
		public void writeTo(DataOutput out) throws IOException {
		//	stamp.writeTo((DataOutputStream) out);
			out.writeInt(stamp.getCounter());
			out.writeLong(stamp.getProcessID());
		}
		
		@Override
		public void readFrom(DataInput in) throws IOException {
//			stamp = new timestamp();
//			stamp.readFrom((DataInputStream) in);
			stamp = new timestamp();
			stamp.setCounter(in.readInt());
			stamp.setProcessID(in.readLong());
		}
		
		public timestamp getTimestamp() {
			return stamp;
		}
		
		@Override
		public int size() {	
			return Global.BYTE_SIZE+stamp.getsize();
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
		switch (evt.getType()) {
		case Event.MSG:
			Message m = (Message) evt.getArg();	
			synchronized (LC_Counter){
			ClockHeader header =  new ClockHeader(LC_Counter.getAndIncrement());
			m.putHeader(ClockHeader.header_id, header);
			return down_prot.down(new Event(Event.MSG,m));
			}
		default:	
			return down_prot.down(evt); 
		}
	}
	@Override
	public Object up(Event evt) {
		switch(evt.getType()){
		case Event.MSG:
			Message m = (Message) evt.getArg();
			ClockHeader CH = (ClockHeader) m.getHeader(ClockHeader.header_id);
			if (CH == null) {
				return up_prot.up(evt);
			}
			synchronized(LC_Counter){
				timestamp localstamp = new timestamp(LC_Counter.get());
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
	
	}
	
	@Override
	public void init() {
		ClassConfigurator.add(ClockHeader.header_id, ClockHeader.class);
	}
}
