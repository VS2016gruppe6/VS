package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;

public class VSBOCThread extends Thread {
	
	private int Thread_ID;
	private VSSenMsg object;
	private VSConnection connect;
	
	public VSBOCThread(int Thread_ID,VSSenMsg object,VSConnection connect){
		this.Thread_ID = Thread_ID;
		this.object = object;
		this.connect = connect;
	}
	
	public void ThreadInit(int Thread_ID,VSSenMsg object,VSConnection connect){
		this.Thread_ID = Thread_ID;
		this.object = object;
		this.connect = connect;
	}
	
	public void run(){
		
		VSBuggyObjectConnection BOC = new VSBuggyObjectConnection(connect);
		try {
			BOC.sendObject(object);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("BOC Thread Error: calling sendObj error");
			e.printStackTrace();
		}
		
	}

}
