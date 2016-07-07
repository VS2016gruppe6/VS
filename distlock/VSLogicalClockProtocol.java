package vsue.distlock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgroups.Event;
import org.jgroups.Global;
import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.stack.Protocol;

import vsue.distlock.VSLamportLockProtocol.LockProtocolHeader;
import vsue.log.Log;

public class VSLogicalClockProtocol extends Protocol {
	private volatile AtomicInteger counter;

	public VSLogicalClockProtocol() {
		counter = new AtomicInteger(0);
	}

	public static class ClockHeader extends Header {
		public static final short header_id = 1501;
		private Timestamp timestamp;

		// Headers need a default constructor for instantiation via reflection.
		public ClockHeader(/* Don't add parameters here! */) {
		}

		public ClockHeader(int stamp) {
			timestamp = new Timestamp(stamp);
		}

		@Override
		public void writeTo(DataOutputStream s) throws IOException {
			timestamp.writeTo(s);
		}

		@Override
		public void readFrom(DataInputStream s) throws IllegalAccessException,
				InstantiationException {
			try {
				timestamp = new Timestamp();
				timestamp.readFrom(s);
			} catch (IOException e) {
				System.err.println("This is an ERROR");
				e.printStackTrace();
				Log.s(e);
			}
		}

		@Override
		public int size() {
			return Global.BYTE_SIZE + timestamp.size();
		}

		public Timestamp getTimestamp() {
			return timestamp;
		}
	}

	// --- Interface for LamportLockProtocol class ---
	public static int getMessageTime(Message m) {
		Header header = m.getHeader(ClockHeader.header_id);
		if (header == null) {
			Log.e("Error: no header found");
			return -1;
		}
		ClockHeader hdr = (ClockHeader) header;
		return (int) hdr.getTimestamp().getCounter();
	}

	// --- Protocol implementation ---
	@Override
	public Object down(Event evt) {
		switch (evt.getType()) {
		case Event.MSG:
			Message msg = (Message) evt.getArg();
			ClockHeader hdr = null;

			// if (msg.getHeader(LockProtocolHeader.header_id) == null) {
			// return down_prot.down(evt);
			// }
			synchronized (counter) {
				// add a clockHeader to the message
				hdr = new ClockHeader(counter.getAndIncrement());

				msg.putHeader(ClockHeader.header_id, hdr);
				evt = new Event(Event.MSG, msg);
				return down_prot.down(evt);
			}
		default:
			return down_prot.down(evt);
		}
	}

	@Override
	public Object up(Event evt) {
		switch (evt.getType()) {
		case Event.MSG:
			Message msg = (Message) evt.getArg();

			ClockHeader clockHeader = (ClockHeader) msg
					.getHeader(ClockHeader.header_id);
			if (clockHeader == null) {
				Log.d("No header found");
				return up_prot.up(evt);
			}

			synchronized (counter) {
				Timestamp localTimestamp = new Timestamp(counter.get());
				Timestamp remoteTimestamp = clockHeader.getTimestamp();
				switch (remoteTimestamp.compareTo(localTimestamp)) {
				case -1:
					break;
				case 1:
					counter.set(remoteTimestamp.getCounter() + 1);
					break;
				case 0:
					Log.e("Error: Equal Timestamps");
				default:
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
