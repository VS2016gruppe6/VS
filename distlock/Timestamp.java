package vsue.distlock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jgroups.Global;
import org.jgroups.util.Streamable;

public class Timestamp implements Comparable<Timestamp>, Streamable {
	private Integer counter;
	private Long processID;
	private String address;

	public Timestamp(Integer counter) {
		this.counter = counter;
		processID = Thread.currentThread().getId();
		address = createAddress();
	}
	
	public Timestamp() {}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(Integer counter) {
		this.counter = counter;
	}

	public Long getProcessID() {
		return processID;
	}

	public void setProcessID(Long processID) {
		this.processID = processID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public int compareTo(Timestamp stamp) {
		if (this.getCounter() < stamp.getCounter()) {
			return -1;
		} else if (this.getCounter() > stamp.getCounter()) {
			return 1;
		} else {
			if (this.getAddress().compareTo(stamp.getAddress()) < 0) {
				return -1;
			} else if (this.getAddress().compareTo(stamp.getAddress()) > 0) {
				return 1;
			} else {
				if (this.getProcessID() < stamp.getProcessID()) {
					return -1;
				} else if (this.getProcessID() > stamp.getProcessID()) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return this.address+"#"+this.processID+"#"+this.counter;
	}

	@Override
	public void readFrom(DataInputStream in) throws IOException,
			IllegalAccessException, InstantiationException {
		address = in.readUTF();
		processID = in.readLong();
		counter = in.readInt();
	}

	@Override
	public void writeTo(DataOutputStream out) throws IOException {
		out.writeUTF(address);
		out.writeLong(processID);
		out.writeInt(counter);
	}

	public int size() {
		return Global.INT_SIZE + Global.LONG_SIZE + address.length();
		// TODO size of address-string right?
	}

	private static String createAddress() {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			return "UnknownHost";
		}
		return addr.getHostName();
	}
}
