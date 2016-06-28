package vsue.replica;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.jgroups.*;

public class VSKeyValueReplicaSever {

	public static void main(String[] args) throws Exception {

		VSKeyValueRequestHandler Replica = new VSKeyValueReplica();

		VSKeyValueRequestHandler ReplicaExport = (VSKeyValueRequestHandler) UnicastRemoteObject
				.exportObject(Replica, 0);

		Registry registry = LocateRegistry.createRegistry(12345);

		registry.bind("service", ReplicaExport);

		//for JGropus
		JChannel channel = new JChannel();
		channel.setReceiver(new ReceiverAdapter() {
			public void receive(Message msg) {
				System.out.println("received msg from " + msg.getSrc() + ": "
						+ msg.getObject());
			}
		});
		channel.connect("gruppe6");
		channel.send(new Message(null, "hello world"));
		//channel.close();

		System.out.println("Replica Service Start!");
	}

}
