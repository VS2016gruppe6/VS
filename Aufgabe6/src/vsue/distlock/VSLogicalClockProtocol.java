package vsue.distlock;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgroups.Event;
import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.stack.Protocol;

public class VSLogicalClockProtocol extends Protocol {
	
	public static class ClockHeader extends Header {
		public static final short header_id = 1501;

		// Headers need a default constructor for instantiation via reflection.
		public ClockHeader(/* Don't add parameters here! */) { }
		
		@Override
		public void writeTo(DataOutput out) throws IOException {
			// XXX IMPLEMENT ME XXX
		}
		
		@Override
		public void readFrom(DataInput in) throws IOException {
			// XXX IMPLEMENT ME XXX
		}
		
		@Override
		public int size() {
			// XXX IMPLEMENT ME XXX
			return 0;
		}
	}
	
	// --- Interface for LamportLockProtocol class ---
	
	public static int getMessageTime(Message m) {
		// XXX IMPLEMENT ME XXX
		return 0;
	}
	
	// --- Protocol implementation ---
	
	@Override
	public Object down(Event evt) {
		// XXX IMPLEMENT ME XXX
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
