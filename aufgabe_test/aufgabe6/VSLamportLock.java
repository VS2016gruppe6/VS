package vsue.distlock;

import org.jgroups.JChannel;
import org.jgroups.Message;

public class VSLamportLock {
	private JChannel channel;
	boolean flag = false;
   public VSLamportLock(JChannel channel) {
     // XXX IMPLEMENT ME XXX
	   this.channel = channel;
   }
   
   
   public synchronized void Notify(){
	   this.notify();
   }
   
   
   
   public void lock() {
     // XXX IMPLEMENT ME XXX
	   Message msg = new Message(null, channel.getAddress(), "lock");
	   try {
		channel.send(msg);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	   try {
		this.wait();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
   }

   
   
   public void unlock() {
     // XXX IMPLEMENT ME XXX
	   Message msg = new Message(null, channel.getAddress(), "unlock");
	   try {
		channel.send(msg);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
}
