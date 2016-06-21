package vsue.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class VSAuctionRMIServer {

	public static void main(String[] args) throws Exception {
		// Remote-Objekt erzeugen
		VSAuctionService service = new VSAuctionServiceImpl();

		// Remote-Objekt exportieren
	//	VSAuctionService service = (VSAuctionService) UnicastRemoteObject
		//		.exportObject(serviceImpl, 0);	
		
		// Remote-Objekt bekannt machen
		Registry registry =LocateRegistry.createRegistry(12345);
		
		registry.bind("service", service);

		System.out.println("Service Start!");


	}

}
