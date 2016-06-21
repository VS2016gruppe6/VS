package vsue.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class VSAuctionServiceImpl extends UnicastRemoteObject implements
		VSAuctionService,VSAuctionEventHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	// Constructor
	public VSAuctionServiceImpl() throws RemoteException {
		super();
	}

	// Create Data Space for Object Auction
	private ArrayList<VSAuction> storeVSAuction = new ArrayList<>();

	@Override
	public void registerAuction(VSAuction auction, int duration,
			VSAuctionEventHandler handler) throws VSAuctionException,
			RemoteException {

		VSAuction user = new VSAuction(auction.getName(),
				auction.getPrice(), duration);

		if (duration < 0) {
			throw new VSAuctionException("Duration is negative");
		}

		if (containAuction(auction.getName()) == true) {
			throw new VSAuctionException("auction exists already");
		}
		handler.handleEvent(VSAuctionEventType.AUCTION_START, auction);
		storeVSAuction.add(user);
	}

	@Override
	public VSAuction[] getAuctions() throws RemoteException {
		VSAuction[] tempVSAuction = new VSAuction[storeVSAuction.size()];
		int counter = 0;
		for (VSAuction element : storeVSAuction) {
			if (element.getDuration() > 0) {
				tempVSAuction[counter++] = element.getVSAuction(); 
			}
		}
		return tempVSAuction;
	}

	// auction exists or not
	public boolean containAuction(String auctionName) throws RemoteException {

		VSAuction[] aktiveVSAuction = this.getAuctions();
		for (VSAuction element : aktiveVSAuction) {
			if (element.getName().equals(auctionName)) {

				return true;
			}
		}

		return false;
	}

	// maximum Preis
	/*public int maxPrice() throws RemoteException {

		VSAuction[] aktiveVSAuction = this.getAuctions();
		int maxPrice = aktiveVSAuction[0].getPrice();

		for (VSAuction element : aktiveVSAuction) {
			if (element.getPrice() > maxPrice) {

				maxPrice = element.getPrice();
			}
		}
		return maxPrice;
	}
	*/

	@Override
	public boolean placeBid(String userName, String auctionName, int price,
			VSAuctionEventHandler handler) throws VSAuctionException,
			RemoteException {
	
		for (VSAuction element : storeVSAuction) {
			if (element.getName().equals(auctionName)){
				if (price > element.price) {
					element.setPrice(price);
					handler.handleEvent(VSAuctionEventType.HIGHER_BID, element);
					return true;
				}
				else{
					return false;
				}				
			} 
			else {
				throw new VSAuctionException("auktion doesn't exist! ");
			}
		}
		return false;
	}
	
	public void handleEvent(VSAuctionEventType event, VSAuction auction) throws RemoteException
	{
		auction.currentState =event;
	}

}
