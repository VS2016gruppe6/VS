package vsue.distlock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.Global;
import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.stack.Protocol;

import vsue.distlock.VSLogicalClockProtocol.ClockHeader;
import vsue.log.Log;

public final class VSLamportLockProtocol extends Protocol {
	private volatile ConcurrentSkipListSet<Timestamp> list = new ConcurrentSkipListSet<Timestamp>();
	private volatile Timestamp requestTimestamp = null;
	private volatile Map<String, Timestamp> recentlyReceivedTimestamps = new ConcurrentHashMap<>();
	private volatile VSLamportLock registeredLock = null;
	private volatile Address ownAddress = null;

	// enum for header
	public enum LockProtocolHeaderType {
		RELEASE, REQUEST, ACK
	}

	private synchronized void checkCritical() {
		Timestamp first = list.first();

		for (Timestamp t : recentlyReceivedTimestamps.values()) {
			if (t.compareTo(first) < 0) {
				return;
			}
		}

		// no smaller timestamp found
		registeredLock.executeCritical();
	}

	private synchronized void setRequestTimestamp(Timestamp timestamp) {
		this.requestTimestamp = timestamp;
	}

	// --- Additional header for Lock request messages ---
	public static class LockProtocolHeader extends Header {
		public static final short header_id = 1500;
		private LockProtocolHeaderType headerType;

		// Headers need a default constructor for instantiation via reflection.
		public LockProtocolHeader(/* Don't add parameters here! */) {
		}

		public LockProtocolHeader(LockProtocolHeaderType headerType) {
			this.headerType = headerType;
		}

		@Override
		public void readFrom(DataInputStream s) {
			try {
				headerType = LockProtocolHeaderType.class.getEnumConstants()[s
						.readByte()];
			} catch (IOException e) {
				Log.s(e);
			}
		}

		@Override
		public void writeTo(DataOutputStream s) {
			try {
				s.writeByte(headerType.ordinal());
			} catch (IOException e) {
				Log.s(e);
			}
		}

		@Override
		public int size() {
			return Global.BYTE_SIZE;
		}

		public LockProtocolHeaderType getHeaderType() {
			return this.headerType;
		}
	}

	// --- Interface to LamportLock class ---
	public void register(VSLamportLock lamportLock) {
		this.registeredLock = lamportLock;
	}

	// --- Protocol implementation ---
	@Override
	public Object down(Event evt) {
		synchronized (this) {
			if (ownAddress == null) {
				ownAddress = getProtocolStack().getChannel().getAddress();
			}
		}
		switch (evt.getType()) {
		case Event.MSG:
			Message msg = (Message) evt.getArg();
			String cmd;
			try {
				cmd = (String) msg.getObject();
				Message message = new Message();
				message.setSrc(ownAddress);
				LockProtocolHeader header = null;

				if (cmd.equals("lock")) {
					header = new LockProtocolHeader(
							LockProtocolHeaderType.REQUEST);
				} else if (cmd.equals("unlock")) {
					header = new LockProtocolHeader(
							LockProtocolHeaderType.RELEASE);
					list.pollFirst();
				}
				message.putHeader(LockProtocolHeader.header_id, header);
				evt = new Event(Event.MSG, message);
				return down_prot.down(evt);
			} catch (Exception e) {

			}
		default:
			return down_prot.down(evt);
		}
	}

	@Override
	public Object up(Event evt) {
		synchronized (this) {
			if (ownAddress == null) {
				ownAddress = getProtocolStack().getChannel().getAddress();
			}
		}
		switch (evt.getType()) {
		case Event.MSG:
			Message msg = (Message) evt.getArg();
			LockProtocolHeader hdr = (LockProtocolHeader) msg
					.getHeader(LockProtocolHeader.header_id);

			Timestamp receivedTimestamp = ((ClockHeader) msg
					.getHeader(ClockHeader.header_id)).getTimestamp();
			recentlyReceivedTimestamps.put(msg.getSrc().toString(),
					receivedTimestamp);

			Object ret = null;
			if (hdr == null) {
				ret = up_prot.up(evt);
			} else {

				switch (hdr.getHeaderType()) {
				case REQUEST:
					if (msg.getSrc().compareTo(ownAddress) == 0) {
						setRequestTimestamp(receivedTimestamp);
					}
					list.add(receivedTimestamp);

					// create ack
					LockProtocolHeader ackHeader = new LockProtocolHeader(
							LockProtocolHeaderType.ACK);
					Message ackMessage = new Message(msg.getSrc(), ownAddress,
							null);
					ackMessage.putHeader(LockProtocolHeader.header_id,
							ackHeader);
					evt = new Event(Event.MSG, ackMessage);

					// send ack
					ret = down_prot.down(evt);
					break;
				case RELEASE:
					// remove the first entry of the list, if RELEASE is
					// received
					if (msg.getSrc().compareTo(ownAddress) != 0) {
						list.pollFirst();
					}
				}
			}
			synchronized (this) {
				if (!list.isEmpty() && requestTimestamp != null
						&& list.first().compareTo(requestTimestamp) == 0) {
					checkCritical();
				}
			}
			return ret;
		case Event.VIEW_CHANGE:
			View receivedView = (View) evt.getArg();
			for (Address address : receivedView.getMembers()) {
				Timestamp timestamp = new Timestamp(0);
				timestamp.setAddress(address.toString());
				recentlyReceivedTimestamps.put(address.toString(), timestamp);
			}
		default:
			return up_prot.up(evt);
		}
	}

	@Override
	public void init() {
		ClassConfigurator.add(LockProtocolHeader.header_id,
				LockProtocolHeader.class);
	}
}
