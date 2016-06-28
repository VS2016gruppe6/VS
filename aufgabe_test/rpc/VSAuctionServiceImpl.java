package vsue.rpc;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.text.html.parser.Element;

public class VSAuctionServiceImpl implements VSAuctionService{//add Serializable

	Timer timer;
	VSAuctionEventHandler winner;
	// Create Data Space for Object Auction
	private ArrayList<VSAuction> storeVSAuction = new ArrayList<>();

	@Override
	public void registerAuction (VSAuction auction, int duration,VSAuctionEventHandler handler) throws VSAuctionException,RemoteException {
		final VSAuctionEventHandler _handler = handler;
		final VSAuction user = new VSAuction(auction.getName(),auction.getPrice(), duration);
		timer = new Timer();
		winner = handler;
		// timer.schedule(new Durationtask(), 1000 * duration);
		if (duration < 0) {
			throw new VSAuctionException("Duration is negative");
		}
		if (containAuction(user.getName()) == true) {
			throw new VSAuctionException("auction exists already");
		}
		storeVSAuction.add(user);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {

				try {
				     
					winner.handleEvent(VSAuctionEventType.AUCTION_END,user);
					storeVSAuction.remove(user);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					System.err.println("unable to call handlerEvent methode");
					System.err.println(e.getMessage());
				}
			}
		}, duration * 1000);
	}

	@Override
	public VSAuction[] getAuctions() throws RemoteException {
		VSAuction[] tempVSAuction = new VSAuction[storeVSAuction.size()];
		int counter = 0;
		for (VSAuction element : storeVSAuction) {
			tempVSAuction[counter++] = element.getVSAuction();
		}

		return tempVSAuction;
	}

	// auction exists or not
	public boolean containAuction(String auctionName) throws RemoteException  {

		VSAuction[] aktiveVSAuction = this.getAuctions();
		for (VSAuction element : aktiveVSAuction) {
			if (element == null)
				continue;
			if (element.getName().equals(auctionName)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean placeBid(String userName, String auctionName, int price,
			VSAuctionEventHandler handler) throws VSAuctionException,
			RemoteException {
		for (VSAuction element : storeVSAuction) {
			if (element.getName().equals(auctionName)) {
				if (price >= element.price) {
					winner = handler;
					winner.handleEvent(VSAuctionEventType.HIGHER_BID, element);
					element.setPrice(price);
					element.setHighstBid(userName);
					return false;
				} else {
//					element.setPrice(price);
//					element.setHighstBid(userName);
					return true;
				}
			}
		}
		throw new VSAuctionException("auktion doesn't exist! ");
	}

}
