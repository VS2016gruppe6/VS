package vsue.rpc;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import vsue.rpc.VSAuction;

public class VSAuction implements Serializable{
	/* The auction name. */
	private final String name;
	private boolean durationOutFlag=false;
	public String highstBid;

	/* The currently highest bid for this auction. */
	int price;

	// VSAuctionEventType lastevent;

	// Implementation
	private int duration;
//	Timer timer = new Timer();

	// VSAuctionEventHandler handler;

	public VSAuction(String name, int startingPrice, int duration) {
		this.name = name;
		this.price = startingPrice;
		this.duration = duration;
	//	timer.schedule(new Durationtask(), 1000 * duration);
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int higherPrice) {
		this.price = higherPrice;
	}

	public void setDurationflag()
	{
		this.durationOutFlag=true;
		System.out.println("finish set");
		System.out.println("flag is"+durationOutFlag+"/n");
	}
	
	// Implementation
	public boolean getDurationflag() {    // if true ,dann duration run out
		return durationOutFlag;
	}
	
	
	public void setHighstBid(String highstBid) {

		this.highstBid = highstBid;
	}

	public VSAuction getVSAuction() {
		return new VSAuction(this.getName(), this.getPrice(),
				this.duration);      //? duration
	}
	
	 public boolean r_equals(Object o){
	    	if(!(o instanceof VSAuction)){
	    		return false;
	    	}else{
	    		VSAuction vsauction=(VSAuction)o;
	    		if(!vsauction.getName().equals(this.getName())||vsauction.getPrice()!=this.getPrice()){
	    			return false;
	    		}
	    		return true;
	    	}
	    }


}