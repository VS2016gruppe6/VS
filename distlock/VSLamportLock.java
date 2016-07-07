package vsue.distlock;

import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.JChannel;
import org.jgroups.Message;

import vsue.log.Log;

public class VSLamportLock {
	private JChannel channel;
	private VSLamportLockProtocol myLockProtocol;
	private boolean b = false;

	public VSLamportLock(JChannel channel) {
		this.channel = channel;
		myLockProtocol = (VSLamportLockProtocol) channel.getProtocolStack()
				.findProtocol(VSLamportLockProtocol.class);
		myLockProtocol.register(this);
	}

	public void executeCritical() {
		synchronized (this) {
			b = true;
			this.notify();

		}
	}

	public void lock() {
		synchronized (this) {
			Log.d("lock()");
			String lock = "lock";
			Message msg = new Message(null, channel.getAddress(), lock);
			b = false;
			try {
				channel.send(msg);
			} catch (ChannelNotConnectedException | ChannelClosedException e) {
				Log.s(e);
			}
			
			while (!b) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	public void unlock() {
		Log.d("unlock()");
		String unlock = "unlock";
		Message msg = new Message(null, channel.getAddress(), unlock);
		try {
			channel.send(msg);
		} catch (ChannelNotConnectedException | ChannelClosedException e) {
			Log.s(e);
		}
	}
}
