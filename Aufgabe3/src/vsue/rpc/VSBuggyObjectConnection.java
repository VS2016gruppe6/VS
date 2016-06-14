package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class VSBuggyObjectConnection extends VSObjectConnection { // simuliation
																	// kommnication
																	// delay.
	private double i = 0.0;
	private int time = 400;   //ms
	VSConnection connect;

	public VSBuggyObjectConnection(VSConnection connect) {
		super(connect);
		this.connect = connect;
	}

	// override
	public void sendObject(Serializable object) throws IOException {
		final Serializable _object = object;  
		i = Math.random();   //[0.0,1.0]
		System.out.println("i = " + i);
		if (i <= 0.25) {
			System.out.println("send object this time normal");
			super.sendObject(_object);
		} else if (i > 0.25 && i <= 0.5) {
			System.out.println("send object after " + time + " thread sleep");
			Timer timer = new Timer();   //??.....put timer outside
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					VSObjectConnection vsconnect = new VSObjectConnection(connect);
					try {
						vsconnect.sendObject(_object);
						this.cancel();     //cancle timer
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, time);

		} else if (i > 0.5 && i <= 0.75) {
			System.out.println("don't send object this time");
			return;
		} else if (i > 0.75 && i <= 1.0) {
			System.out.println("don't send object after " + time
					+ "thread sleep");
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					return;
				}
			}, time);

		}
	}
}