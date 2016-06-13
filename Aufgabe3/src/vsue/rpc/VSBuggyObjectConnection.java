package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class VSBuggyObjectConnection extends VSObjectConnection {
	private int i = 0;
	private int time = 400;
	VSConnection connect;
	public VSBuggyObjectConnection(VSConnection connect) {
		super(connect);
		this.connect = connect;
		
	}
	//override
	public void sendObject(Serializable object) throws IOException{
		final Serializable _object = object;
		i++;
		System.out.println("i = "+i);	
	if(i % 3 == 0){
		System.out.println("send object after "+time+" thread sleep");
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				VSObjectConnection vsconnect = new VSObjectConnection(connect);
				try {
					vsconnect.sendObject(_object);
					this.cancel();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, time);
		
	}
	else if (i%3 == 1){
		System.out.println("don't send object this time");
		return;
	}
	else if (i%3 == 2){
		System.out.println("don't send object after "+time+"thread sleep");
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