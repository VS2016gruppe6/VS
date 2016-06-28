package vsue.rpc;

import java.util.TimerTask;

class VSTimeoutHandler extends TimerTask {
	private int requestNr;
	
	 public VSTimeoutHandler(int requestNr){
		 this.requestNr=requestNr;
	 }
	 
	public void run() {
		System.err.println(requestNr+"Timeout!");
		
		
	}
}
