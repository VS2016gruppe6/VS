package vsue.replica;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;



//----------------------------------------------code added 21.06

public class VSKeyValueReplica implements VSKeyValueRequestHandler{

	private Hashtable<String, String> VSKeyValue = new Hashtable<String, String>();
	
	private VSKeyValueReplyHandler ReplyHandler;
	
	@Override
	public void handleRequest(VSKeyValueRequest request) throws RemoteException {
		switch (request.GetRequestingOperration()){
			case PUT:
				VSKeyValue.put(request.GetOperationArg1(), request.GerOperationArg2());
				break;
			case GET:
				VSKeyValueReply Reply1 = new VSKeyValueReply(this,
															VSKeyValueOperation.GET,
															VSKeyValue.get(request.GetOperationArg1()),
															(long)0);
				ReplyHandler.handleReply(Reply1);
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
				ReplyHandler.handleReply(Reply2);
				}
				else{
					VSKeyValueReply Reply2 = new VSKeyValueReply(this,
																VSKeyValueOperation.EXISTS,
																VSKeyValue.get(request.GetOperationArg1()),
																System.currentTimeMillis());	
				ReplyHandler.handleReply(Reply2);	
				}
				break;
			
			case RELIABLE_EXISTS:
				break;				
							
			default:
				break;
				
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		VSKeyValueReplica Replica = new VSKeyValueReplica();

		UnicastRemoteObject.exportObject(Replica, 0);
		
		Registry registry =LocateRegistry.createRegistry(12345);
		
		registry.bind("service", Replica);

		System.out.println("Replica Service Start!");
	}


}
