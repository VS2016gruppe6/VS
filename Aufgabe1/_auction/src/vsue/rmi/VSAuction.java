package vsue.rmi;

import java.util.Timer;
import java.util.TimerTask;

public class VSAuction
{
    /* The auction name. */
    private final String name;
 
    /* The currently highest bid for this auction. */
    int price; 
    
    VSAuctionEventType currentState;
    
    //Implementation 
    private int duration; 
	Timer  timer = new Timer();
	
	VSAuctionEventHandler handler;
        
    public VSAuction(String name, int startingPrice,int duration)
    {
        this.name = name;
        this.price = startingPrice;
		this.duration=duration;
		timer.schedule(new Durationtask(), 1000);
    }

    public String getName()
    {
        return name;
    }

    public int getPrice()
    {
        return price;
    }
    
    public void setPrice(int higherPrice)
    {
        this.price=higherPrice;
    }
    
  //Implementation 
    public int getDuration()
    {
    	return duration;
    }
    
    public VSAuction getVSAuction(){
		return new VSAuction(this.getName(),this.getPrice(),this.getDuration()); 
	}
	
	private class Durationtask extends TimerTask{

		@Override
		public void run() {
			if(duration >0) duration--;
			else{ 
				timer.cancel();
			}	
			
		}
		
		
	}
}
