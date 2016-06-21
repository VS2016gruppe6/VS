package vsue.replica;

import java.rmi.RemoteException;
import java.util.Hashtable;

//----------------------------------------------code added 21.06

public class VSKeyValueReplica implements VSKeyValueRequestHandler{

	private Hashtable<String, String> VSKeyValue = new Hashtable<String, String>();
	
	@Override
	public void handleRequest(VSKeyValueRequest request) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
