package vsue.distlock;

import org.jgroups.JChannel;
import org.jgroups.Message;

public class VSLamportLock {
	private JChannel channel;
	boolean flag = false;
	private VSLamportLockProtocol myLockProtocol;
   public VSLamportLock(JChannel channel) {
     // XXX IMPLEMENT ME XXX
	   this.channel = channel;
		myLockProtocol = (VSLamportLockProtocol) channel.getProtocolStack()
				.findProtocol(VSLamportLockProtocol.class);
		myLockProtocol.register(this);
   }
   
   
   public synchronized void Notify(){
	   synchronized(this){
		   flag = true;
		   this.notify();
	   }
	
   }
   
   //TODO   Change structure  rewrite sendRequest in protocolschicht und move channel.send to protocol usw
   
   public void lock() {
     // XXX IMPLEMENT ME XXX
	   synchronized(this){
	   String lock = "lock";
	   Message msg = new Message(null, null, lock);
	   flag = false;
		   try {
			  // System.out.println("send lock");
			channel.send(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//	   while(!flag){	   
//		   try {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	   }
   }
	   
   }

   
   
   public void unlock() {
     // XXX IMPLEMENT ME XXX
	   String unlock = "unlock";
	   Message msg = new Message(null, channel.getAddress(), unlock);
	   try {
		channel.send(msg);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
}
