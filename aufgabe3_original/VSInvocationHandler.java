package vsue.rpc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class VSInvocationHandler implements InvocationHandler, Serializable {

	private VSRemoteReference remote; // /remoteReference

	static final private int maxNr = 5;
	static final private int timeout = 1;

	public VSInvocationHandler(VSRemoteReference remote) {

		this.remote = remote;

	}

	// fuell alle lokalen aufrufe am Proxy
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
	{
		Socket socket = null;
		VSObjectConnection connect;
		VSRevMsg revMsg = null;
		socket = new Socket(remote.getHost(), remote.getPort());
		Object[] toSend;
		if (args == null) 
		{
			toSend = new Object[0];
		} 
		else 
		{
			toSend = args; // .clone();
			for (int i = 0; i < toSend.length; i++) 
			{
				if (toSend[i] instanceof Remote) 
				{
					Remote stub = VSRemoteObjectManager.getInstance().getproxy((Remote) toSend[i]);
					toSend[i] = stub;
				}
			}
		}

		VSSenMsg senMsg = new VSSenMsg(remote.getObjectID(),method.toGenericString(), toSend,0,0);
		connect = new VSObjectConnection(new VSConnection(socket));

		for (int i = 0; i < maxNr; i++) 
		{
			try 
			{
				// Anfrage durch Socket senden
				senMsg.setSeq(i);
				connect.sendObject(senMsg);
			} catch (Exception e) 
			{
				System.out.println("unable to send proxy in invocationhandler involke!");
			}
			socket.setSoTimeout(timeout);
			try {

				revMsg = (VSRevMsg) connect.receiveObject();
				if(revMsg.getID() == senMsg.getID())
					if(revMsg.getSeq() == i)
				break;
			} catch (SocketTimeoutException e) {
				System.out.println("timeout !"+i);
				System.out.println(e.getMessage());
				continue;
			}

			// antwort verwerten
			
			
		
			
		}
		if(revMsg != null)
		{
			Throwable exc = revMsg.getFehler();
			if (exc != null) 
			{
				Type[] allowedexceptiontypescollection = method
						.getGenericExceptionTypes();
				for (Type singleallowedexception : allowedexceptiontypescollection) 
				{
					if (((Class<?>) singleallowedexception)
							.isAssignableFrom(exc.getClass())) 
					{
						throw exc;
					}
				}
			}return revMsg.getResult();
		}
		else
			throw new RemoteException("unable to get response to requests");
		
	}
}
