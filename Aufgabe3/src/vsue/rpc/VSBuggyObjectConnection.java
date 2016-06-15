package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class VSBuggyObjectConnection extends VSObjectConnection {
	private double i = 0.0;
	private int time = 4000;
	VSConnection connect;

	public VSBuggyObjectConnection(VSConnection connect) {
		super(connect);
		this.connect = connect;

	}

	// override
	public void sendObject(Serializable object) throws IOException {
		final Serializable _object = object;   //?  
		i = Math.random();   //[0.0, 1.0]
		i = 0.8;
		long tmp_time = (long) (Math.random() * (double) time);
		System.out.println("i = " + i);
		if (i <= 0.70) {									//send object this time normal	
			System.out.println("send object this time normal");
			super.sendObject(_object);
		} else if (i > 0.7 && i <= 0.85) {     
			System.out.println("send object after " + tmp_time
					+ " thread sleep");
			try {
				VSObjectConnection vsconnect = new VSObjectConnection(connect);
				long sleepBegin = System.currentTimeMillis();     //begin
				Thread.sleep(tmp_time);
				long b = System.currentTimeMillis();     //begin

			//	System.out.println("sleepTime= " + (b -sleepBegin));
							
				vsconnect.sendObject(_object);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (i > 0.85 && i <= 0.9) {
			System.out.println("don't send object this time");
			return;
		} else if (i > 0.9 && i <= 1.0) {
			System.out.println("don't send object after " + tmp_time
					+ " thread sleep");
			try {
				Thread.sleep(tmp_time);
				return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}