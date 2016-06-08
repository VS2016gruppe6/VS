package vsue.rmi;


public class VSAuctionRMIServer {

	public static void main(String[] args) throws Exception {
		// Remote-Objekt erzeugen
		VSAuctionService serviceimpl = new VSAuctionServiceImpl();    //schnittstell erstellen

		VSAuctionService  vsAuctionService = (VSAuctionService)
				UnicastRemoteObject.exportObject(serviceimpl, 0);
		
//		VSAuctionService  vsAuctionService = (VSAuctionService)
//				UnicastRemoteObject.exportObject(new VSAuctionServiceImpl(), 0);
		
//		
		// Remote-Objekt bekannt machen
		Registry registry =LocateRegistry.createRegistry(12345);
		
		registry.bind("service", vsAuctionService);

		System.out.println("Service Start!");


	}

}
