package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;

public class VSBuggyObjectConnection extends VSObjectConnection{
	private int i = 0;
	private int time = 800;
	public VSBuggyObjectConnection(VSConnection connect) {
		super(connect);
	}
	//override
	public void sendObject(Serializable object) throws IOException{
		i++;
		System.out.println("i = "+i);	
	if(i % 2 == 0){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.err.println("unable to threadsleep");
			System.out.println(e.getMessage());
		}
		System.out.println("send object after "+time+" thread sleep");
		super.sendObject(object);
	}
	else{
		System.out.println("don't send object this time");
		return;
	}
}
}