package vsue.distlock;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.stack.Protocol;

import vsue.distlock.VSLogicalClockProtocol.ClockHeader;


public final class VSLamportLockProtocol extends Protocol {
	public enum LockProtocolHeaderType{
		RELEASE,REQUEST,ACK
	}
	VSLamportLock registeredLock;
	private volatile Address localAddress;
	private volatile timestamp localTimeStamp;
	private volatile ConcurrentSkipListSet<timestamp> list = new ConcurrentSkipListSet<timestamp>();
	private volatile Map<String,timestamp> ReceivedTimeStampMap = new ConcurrentHashMap<>();
	private volatile AtomicInteger sum;
	
	public synchronized void setlocalTimestamp(timestamp stamp){
		this.localTimeStamp = stamp;
	}
	// --- Additional header for lock-request messages ---
	public static class LockProtocolHeader extends Header {
		public static final short header_id = 1500;
		LockProtocolHeaderType lpHeadType;
		
		
		// Headers need a default constructor for instantiation via reflection.
		public LockProtocolHeader(/* Don't add parameters here! */) { }
		
		public LockProtocolHeader(LockProtocolHeaderType lpHeadType){
			this.lpHeadType = lpHeadType;
		}
		
		public LockProtocolHeaderType getHeaderType(){
			return this.lpHeadType;
		}
		@Override
		public void readFrom(DataInput in) throws IOException {
			// XXX IMPLEMENT ME XXX
			lpHeadType = LockProtocolHeaderType.class.getEnumConstants()[in.readByte()];
		
		}
		
		@Override
		public void writeTo(DataOutput out) throws IOException {
			out.writeByte(lpHeadType.ordinal());
		}
		
		@Override
		public int size() {
			return 0;
		}
	}
	
	// --- Interface to LamportLock class ---

	// XXX IMPLEMENT ME XXX
	public void register(VSLamportLock lock){
		this.registeredLock = lock;
	}

	// --- Protocol implementation ---
	@Override
	public Object down(Event evt) {
		// XXX IMPLEMENT ME XXX
		synchronized(this){
			if(localAddress == null){
				localAddress = getProtocolStack().getChannel().getAddress();
			}
		}
			switch (evt.getType()) {
			case Event.MSG:
				Message msg = (Message) evt.getArg();
				String order = (String) msg.getObject();
			
				LockProtocolHeader header = null;
				if(order.equals("lock")){
					 header = new LockProtocolHeader(LockProtocolHeaderType.REQUEST);
				}else if(order.equals("unlock")){
					 header = new LockProtocolHeader(LockProtocolHeaderType.RELEASE);
					 list.pollFirst();
				}
				Message ReMsg = new Message();
				ReMsg.putHeader(LockProtocolHeader.header_id, header);
				return down_prot.down(new Event(Event.MSG,ReMsg));
			default:
				return down_prot.down(evt);
			}	
		
	}
	
	@Override
	public Object up(Event evt) {
		// XXX IMPLEMENT ME XXX
		synchronized(this){
			if(localAddress == null){
				localAddress = getProtocolStack().getChannel().getAddress();
			}
		}
			switch (evt.getType()) {
			case Event.MSG:
				Message msg = (Message) evt.getArg();
				LockProtocolHeader header = (LockProtocolHeader) msg.getHeader(LockProtocolHeader.header_id);
				timestamp receivedStamp = ((ClockHeader)msg.getHeader(ClockHeader.header_id)).getTimestamp();
		
				switch (header.getHeaderType()){
				case REQUEST:
					if(msg.getSrc().compareTo(localAddress) == 0){
						setlocalTimestamp(receivedStamp);
					}
					list.add(receivedStamp);
					//generieren ack if request received
					LockProtocolHeader Ackheader =  new LockProtocolHeader(LockProtocolHeaderType.ACK);
					Message AckMsg = new Message();
					AckMsg.putHeader(LockProtocolHeader.header_id, Ackheader);
					return down_prot.down(new Event(Event.MSG,AckMsg));
				case RELEASE:
					list.pollFirst();	
					break;
				case ACK:
					// store received acks, when all acks from all members received, then notify()
					ReceivedTimeStampMap.put(msg.getSrc().toString(), receivedStamp);
					View receivedView = (View) evt.getArg();
					for(Address address : receivedView.getMembers()){
						if(ReceivedTimeStampMap.containsKey(address.toString())){
							sum.incrementAndGet();
						}							
					}	
					synchronized (this){
						//  notify the wait in VSLamportLock
						if(list.first().compareTo(localTimeStamp) == 0 &&
								  	sum.intValue() == receivedView.size()){
							registeredLock.Notify();
						}
					}
					break;	
				}
			default:
				return up_prot.up(evt);
			}

	}
	
	@Override
	public void init() {
		ClassConfigurator.add(
			LockProtocolHeader.header_id, LockProtocolHeader.class);
	}
}
