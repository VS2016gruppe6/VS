package vsue.replica;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;



//----------------------------------------------code added 21.06

public class VSKeyValueReplica implements VSKeyValueRequestHandler{

	private Hashtable<String, String> VSKeyValue = new Hashtable<String, String>();
	

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
