package vsue.replica;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.protocols.SEQUENCER;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.protocols.SEQUENCER.SequencerHeader;;


//----------------------------------------------code added 21.06

public class VSKeyValueReplica implements VSKeyValueRequestHandler{

	private Hashtable<String, String> VSKeyValue = new Hashtable<String, String>();
	

	@SuppressWarnings("null")
	@Override
	public void handleRequest(VSKeyValueRequest request) throws RemoteException {
		
		//send request if received request from client---------------------28.06
		JChannel channel = null;
		
		//long seqNr = ((SequencerHeader) msg.getHeader(ClassConfigurator.getProtocolId(SEQUENCER.class))).getSeqno();
		try {
			channel = new JChannel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			channel.connect("gruppe 6");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			channel.send(new Message(null, request));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//handle request
		switch (request.GetRequestingOperration()){
			case PUT:
				VSKeyValue.put(request.GetOperationArg1(), request.GerOperationArg2());
				break;
			case GET:
				VSKeyValueReply Reply1 = new VSKeyValueReply(this,
															VSKeyValueOperation.GET,
															VSKeyValue.get(request.GetOperationArg1()),
															(long)0);
				System.out.print(VSKeyValue.get(request.GetOperationArg1()));
				request.GetRequestingClient().handleReply(Reply1);
				break;
				
			case DELETE:
				VSKeyValue.remove(request.GetOperationArg1());
				break;
				
			case EXISTS:
				if(VSKeyValue.containsKey(request.GetOperationArg1())){
					VSKeyValueReply Reply2 = new VSKeyValueReply(this,
																VSKeyValueOperation.EXISTS,
																VSKeyValue.get(request.GetOperationArg1()),
																System.currentTimeMillis());
					request.GetRequestingClient().handleReply(Reply2);
				}
				else{
					VSKeyValueReply Reply2 = new VSKeyValueReply(this,
																VSKeyValueOperation.EXISTS,
																VSKeyValue.get(request.GetOperationArg1()),
																System.currentTimeMillis());	
					request.GetRequestingClient().handleReply(Reply2);	
				}
				break;
			
			case RELIABLE_EXISTS:
				break;				
							
			default:
				break;
				
		}
	}


}
