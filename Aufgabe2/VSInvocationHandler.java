package vsue.rpc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;
import java.rmi.Remote;

public class VSInvocationHandler implements InvocationHandler,Serializable {
	
	private VSRemoteReference remote;  ///remoteReference
	
	//static final private int maxNr=5;
	
	public VSInvocationHandler(VSRemoteReference remote) {
	
		this.remote = remote;
				
	}
	
	//fuell alle lokalen aufrufe am Proxy
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		Socket socket=null;
		VSObjectConnection connect;
		VSRevMsg revMsg=null;
		
		
		socket=new Socket(remote.getHost(),remote.getPort());
		Object[] toSend ;
		if(args == null)
		{
			toSend = new Object[0];
		}else
		{
		 toSend = args;  //.clone();
		for(int i=0;i<toSend.length;i++)
		{
			if(toSend[i] instanceof Remote)
			{
				Remote stub = VSRemoteObjectManager.getInstance().getproxy((Remote)toSend[i]);
					toSend[i]=stub;
			}
		}
		}
		
		VSSenMsg senMsg=new VSSenMsg(remote.getObjectID(), method.toGenericString(),toSend);														
		
		if(socket.isBound()){
			//System.out.println("invocationhandler invoke socket is bound " + remote.getHost() + " " + remote.getPort());      //local port socket.getLocalPort()
		}
		
		connect=new VSObjectConnection(new VSConnection(socket));
		try 
		{
			// Anfrage durch Socket senden
			  connect.sendObject(senMsg);
			}catch(Exception e)
			{
				  System.out.println("unable to send proxy in invocationhandler involke!");
			}
		
		try {
				//antwort empfangen
				revMsg=(VSRevMsg)connect.receiveObject();
//				if(revMsg!=null){
//					//System.out.println("revMsg received successfully!");
//						//antwort verwerten
				Throwable exc = revMsg.getFehler();		
				if(exc != null)
				{
				Type[] allowedexceptiontypescollection = method.getGenericExceptionTypes();
						for(Type singleallowedexception : allowedexceptiontypescollection)
						{
							if(((Class<?>)singleallowedexception).isAssignableFrom(exc.getClass()))
							{
								throw exc;
							}
						}
						}

				}catch(Exception e){
					e.printStackTrace();
				}

		//System.out.println("can run before revMsg.getResult()");
		return  revMsg.getResult();	
	}
}
