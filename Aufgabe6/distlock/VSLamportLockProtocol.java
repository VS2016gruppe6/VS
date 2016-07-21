package vsue.distlock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.Global;
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
	private volatile VSLamportLock registeredLock;
	private volatile Address localAddress ;
	private volatile timestamp localTimeStamp = new timestamp();
	private volatile ConcurrentSkipListSet<timestamp> list = new ConcurrentSkipListSet<timestamp>();
	private volatile Map<String,timestamp> ReceivedTimeStampMap = new ConcurrentHashMap<>();

	
	private synchronized void checkCritical() {
		timestamp first = list.first();

		for (timestamp t : ReceivedTimeStampMap.values()) {
			if (t.compareTo(first) < 0) {	
				return;
			}
		}
		//System.out.println("critical notify");
		// no smaller timestamp found
		registeredLock.Notify();
	}
	

	public synchronized void setlocalTimestamp(timestamp stamp){
		if(stamp != null){
			this.localTimeStamp = stamp;
		}
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
			lpHeadType = LockProtocolHeaderType.class.getEnumConstants()[in.readInt()];
		
		}
		
		@Override
		public void writeTo(DataOutput out) {
			try{
			out.writeInt(lpHeadType.ordinal());}
			catch(Exception e){
				//System.out.println("break point1");
				e.printStackTrace();
				
			}
		}
		
		@Override
		public int size() {		
			return 4;
			//return Global.BYTE_SIZE;
		}
	}
	
	// --- Interface to LamportLock class ---

	// XXX IMPLEMENT ME XXX
	public void register(VSLamportLock lock){
		this.registeredLock = lock;
	}

	
//	public void sendRequest(String type) {
//		
//	}
	
	// --- Protocol implementation ---
	@Override
	public Object down(Event evt) {
		// XXX IMPLEMENT ME XXX
		try{
		synchronized(this){
			if(localAddress == null){
				localAddress = getProtocolStack().getChannel().getAddress();
				localTimeStamp.setAddress(localAddress);
			}
		}
			switch (evt.getType()) {
			case Event.MSG:
				//System.out.println("lamportprotocol di");
				Message msg = (Message) evt.getArg();
				Message Remsg = new Message();
				Remsg.setSrc(localAddress);
				LockProtocolHeader header = null;//=null;
				String order = null;
				try {
					order = (String) msg.getObject();
				} catch (Exception e) {
					return down_prot.down(evt);
				}
					
					if (order == null)
						return down_prot.down(evt);
					
					if(order.equals("lock")){
						//System.out.println("received lock");
						 header = new LockProtocolHeader(LockProtocolHeaderType.REQUEST);
					}else if(order.equals("unlock")){
						//System.out.println("received unlock");
						 header = new LockProtocolHeader(LockProtocolHeaderType.RELEASE);
						 list.pollFirst();
					} 
			
				
				Remsg.putHeader(LockProtocolHeader.header_id, header);
				//System.out.println("point 2");
				return down_prot.down(new Event(Event.MSG,Remsg));
			default:
				return down_prot.down(evt);
			}	
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Object up(Event evt) {
		try {
			// XXX IMPLEMENT ME XXX
			synchronized(this){
				if(localAddress == null){
					localAddress = getProtocolStack().getChannel().getAddress();
					localTimeStamp.setAddress(localAddress);
				}
				switch (evt.getType()) {
				case Event.MSG:
					//System.out.println("point1");
					Message msg = (Message) evt.getArg();
					
					//System.out.println("point2");
					LockProtocolHeader header = (LockProtocolHeader) msg.
							getHeader(LockProtocolHeader.header_id);
					//System.out.println("point3");
					timestamp receivedStamp = ((ClockHeader)msg.
							getHeader(ClockHeader.header_id)).getTimestamp();
					//System.out.println("point4");

					ReceivedTimeStampMap.put(msg.getSrc().toString(),receivedStamp);//localTimeStamp

					Object ret = new Object();
						if(header == null){
							return up_prot.up(evt);
						}else{
							switch (header.getHeaderType()){
							case REQUEST:
		
								if(msg.getSrc().compareTo(localAddress) == 0){
									setlocalTimestamp(receivedStamp);
								}
								list.add(receivedStamp);
								//generieren ack if request received
								LockProtocolHeader Ackheader =  new LockProtocolHeader(LockProtocolHeaderType.ACK);
								Message AckMsg = new Message(msg.getSrc(), localAddress,null);
								AckMsg.putHeader(LockProtocolHeader.header_id, Ackheader);
								ret = down_prot.down(new Event(Event.MSG,AckMsg));
								
								break;
							case RELEASE:
								if(msg.getSrc().compareTo(localAddress) != 0){
									list.pollFirst();
								}
								break;
							}
		
							synchronized (this){
								if(!list.isEmpty() && localTimeStamp != null
										&& list.first().compareTo(localTimeStamp) == 0){
									//System.out.println("point XXXXXXX");
									checkCritical();
								}
							}
						}
						
						return ret;
						

				default:
					//System.out.println("message up ");
					return up_prot.up(evt);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void init() {
		ClassConfigurator.add(
			LockProtocolHeader.header_id, LockProtocolHeader.class);
	}
}
