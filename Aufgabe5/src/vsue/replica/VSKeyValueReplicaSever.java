package vsue.replica;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class VSKeyValueReplicaSever {
	 
	public static void main(String[] args) throws Exception {

		VSKeyValueRequestHandler Replica = new VSKeyValueReplica();

		VSKeyValueRequestHandler ReplicaExport = (VSKeyValueRequestHandler) UnicastRemoteObject.exportObject(Replica, 0);
				
		Registry registry =LocateRegistry.createRegistry(12345);
		
		registry.bind("service", ReplicaExport);
		
		System.out.println("Replica Service Start!");
	}

}
