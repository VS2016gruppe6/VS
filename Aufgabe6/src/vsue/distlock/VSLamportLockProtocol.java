package vsue.distlock;

import java.io.DataInput;
import java.io.DataOutput;

import org.jgroups.Event;
import org.jgroups.Header;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.stack.Protocol;
import org.jgroups.util.Util;

public final class VSLamportLockProtocol extends Protocol {
	
	// --- Additional header for lock-request messages ---
	public static class LockProtocolHeader extends Header {
		public static final short header_id = 1500;
		
		
		
		// Headers need a default constructor for instantiation via reflection.
		public LockProtocolHeader(/* Don't add parameters here! */) { }

		@Override
		public void readFrom(DataInput in) {
			// XXX IMPLEMENT ME XXX
		}
		
		@Override
		public void writeTo(DataOutput out) {
			byte[] Output = null;
			try {
				Output = Util.objectToByteBuffer();
				out.write(Output);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		@Override
		public int size() {
			return 0;
		}
	}
	
	// --- Interface to LamportLock class ---

	// XXX IMPLEMENT ME XXX


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
		ClassConfigurator.add(
			LockProtocolHeader.header_id, LockProtocolHeader.class);
	}
}
