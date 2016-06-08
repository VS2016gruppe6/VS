package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class VSAuctionServer {

	public static void main(String[] args) throws Exception {
		// Remote-Objekt erzeugen
		VSAuctionService serviceimpl = new VSAuctionServiceImpl();    //schnittstell erstellen
		
		VSAuctionService  vsAuctionService = (VSAuctionService)(VSRemoteObjectManager.getInstance().exportObject(serviceimpl) );
		
		// Remote-Objekt bekannt machen
		Registry registry =LocateRegistry.createRegistry(1234);
		
		System.out.println("Service Start!");
		
		registry.bind("service", vsAuctionService);
	}

}
