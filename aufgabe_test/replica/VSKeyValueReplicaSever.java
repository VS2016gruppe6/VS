package vsue.replica;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.jgroups.*;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.protocols.SEQUENCER;
import org.jgroups.protocols.SEQUENCER.SequencerHeader;
import org.jgroups.stack.ProtocolStack;

public class VSKeyValueReplicaSever {

	public static void main(String[] args) throws Exception {
		

		final VSKeyValueRequestHandler Replica = new VSKeyValueReplica();

		VSKeyValueRequestHandler ReplicaExport = (VSKeyValueRequestHandler) UnicastRemoteObject
				.exportObject(Replica, 0);

		Registry registry = LocateRegistry.createRegistry(12346);

		registry.bind("service", ReplicaExport);

		//for JGropus
		@SuppressWarnings("resource")
		JChannel channel = new JChannel();
		channel.connect("gruppe6");
		ProtocolStack protocolStack = channel.getProtocolStack();
		protocolStack.addProtocol(new SEQUENCER());
		//set receiver and call handlerequest()--------------28.06
		channel.setReceiver(new ReceiverAdapter() {
			public void receive(Message msg) {
				final VSKeyValueRequest _request;
				System.out.println("received msg from " + msg.getSrc() + ": "	+ msg.getObject());
				_request = (VSKeyValueRequest) msg.getObject();
			//	long seqNr = ((SequencerHeader) msg.getHeader(ClassConfigurator.getProtocolId(SEQUENCER.class))).getSeqno();
				try {
					Replica.handleRequest(_request);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		

		//channel.close();

		System.out.println("Replica Service Start!");
	}

}
