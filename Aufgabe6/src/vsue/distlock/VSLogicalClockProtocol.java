package vsue.distlock;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgroups.Event;
import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.stack.Protocol;
import org.jgroups.util.Util;

import vsue.distlock.FIFOUnicast.SimulatedUnicastProtocolHeader;

public class VSLogicalClockProtocol extends Protocol {
	
	public int LC_Counter=1;
	
	public static class ClockHeader extends Header {
		public static final short header_id = 1501;
		public int HeaderCounter;
		// Headers need a default constructor for instantiation via reflection.
		public ClockHeader(/* Don't add parameters here! */) { }
		
		public ClockHeader(int Counter){
			HeaderCounter = Counter;
		}
		
		@Override
		public void writeTo(DataOutput out) throws IOException {
			byte[] Output = null;
			try {
				Output = Util.objectToByteBuffer(HeaderCounter);
				out.write(Output);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		@Override
		public void readFrom(DataInput in) throws IOException {
			byte[] Input = null;
			try {
				Input = Util.objectToByteBuffer(in);
				this.HeaderCounter = (int) Util.objectFromByteBuffer(Input, 0, size());
			} catch (Exception e) {
				e.printStackTrace();
			}						
		}
		
		@Override
		public int size() {	
			return 4;
		}
	}

	// --- Interface for LamportLockProtocol class ---
	
	public static int getMessageTime(Message m) {
		ClockHeader header = (ClockHeader) m.getHeader(ClockHeader.header_id);
		if(header != null)
			return header.HeaderCounter;
		else
			return 0;
	}
	
	// --- Protocol implementation ---
	
	@Override
	public Object down(Event evt) {
		switch (evt.getType()) {
		case Event.MSG:
			Message m = (Message) evt.getArg();
			ClockHeader header =  new ClockHeader(LC_Counter);
			m.putHeader(ClockHeader.header_id, header);
			LC_Counter++;
			return down_prot.down(new Event(Event.MSG,m));
		}
		return down_prot.down(evt); 
	}
	
	@Override
	public Object up(Event evt) {
		switch(evt.getType()){
		case Event.MSG:
			Message m = (Message) evt.getArg();
			ClockHeader CH = (ClockHeader) m.getHeader(ClockHeader.header_id);
			
			if (CH.HeaderCounter > LC_Counter){
				LC_Counter = CH.HeaderCounter + 1;
			}
			else
				LC_Counter++;
			Message Package = (Message) m.getObject();
			return up_prot.up(new Event(Event.MSG,Package));
		}
		return up_prot.up(evt);
	}
	
	@Override
	public void init() {
		ClassConfigurator.add(ClockHeader.header_id, ClockHeader.class);
	}
}
