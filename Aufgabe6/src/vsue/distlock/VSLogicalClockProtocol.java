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
	
	public int ProtocolCounter=0;
	
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
			try {
				Util.writeObject(this, out);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		
		@Override
		public void readFrom(DataInput in) throws IOException {
			ClockHeader Input = null;
			try {
				Input = (ClockHeader) Util.readObject(in);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HeaderCounter = Input.HeaderCounter;	
		}
		
		@Override
		public int size() {	
			// XXX IMPLEMENT ME XXX
			return 0;
		}
	}
	
	// --- Interface for LamportLockProtocol class ---
	
	public static int getMessageTime(Message m) {
		ClockHeader header = (ClockHeader) m.getHeader((short) 1501);
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
			ClockHeader header =  
			break;
		}
		return down_prot.down(evt); 
	}
	
	@Override
	public Object up(Event evt) {
		// XXX IMPLEMENT ME XXX
		
		return up_prot.up(evt);
	}
	
	@Override
	public void init() {
		ClassConfigurator.add(ClockHeader.header_id, ClockHeader.class);
	}
}
