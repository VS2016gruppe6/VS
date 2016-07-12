package vsue.distlock;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgroups.Event;
import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.stack.Protocol;
import org.jgroups.util.Util;



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
			stamp.writeTo((DataOutputStream) out);
//			byte[] Output = null;
//			try {
//				Output = Util.objectToByteBuffer(HeaderCounter);
//				out.write(Output);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}		
		}
		
		@Override
		public void readFrom(DataInput in) throws IOException {
			stamp.readFrom((DataInputStream) in);
//			byte[] Input = null;
//			try {
//				Input = Util.objectToByteBuffer(in);
//				this.HeaderCounter = (int) Util.objectFromByteBuffer(Input, 0, size());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}						
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
			return header.getTimestamp().getCounter();
		else
			return 0;
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
		}	
		return down_prot.down(evt); 
	}
	
	@Override
	public Object up(Event evt) {
		switch(evt.getType()){
		case Event.MSG:
			Message m = (Message) evt.getArg();
			ClockHeader CH = (ClockHeader) m.getHeader(ClockHeader.header_id);
			synchronized(LC_Counter){
				timestamp localstamp = new timestamp(LC_Counter.get());
				switch(CH.getTimestamp().compareTo(localstamp)) {
				case -1:
					LC_Counter = CH.g + 1;
					break;
				case 1:
					LC_Counter.incrementAndGet();
					Message Package = (Message) m.getObject();
					Event evt = new Event(Event.MSG,Package);
				case 0:
				default :
					
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
