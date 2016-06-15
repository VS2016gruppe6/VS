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

	private VSRemoteReference remote; // / remoteReference

	static final private int maxNr = 10;
	private int requestId = 0;
	private int socketTimeout = 2000;

	public VSInvocationHandler(VSRemoteReference remote) {
		this.remote = remote;
	}

	// fuell alle lokalen aufrufe am Proxy
	@SuppressWarnings("resource")
	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
		
		long t_begin = 0;   //time for test begin
		long runTime= 0;
		int resttime = socketTimeout; 
		
		// init
		requestId++;   // every time requestId add 1
		Socket socket = null;
		socket = new Socket(remote.getHost(), remote.getPort()); // socker init
		
		//-------------------------------------- Code quoted 15.06 12:06
		//VSBuggyObjectConnection connect = new VSBuggyObjectConnection(new VSConnection(socket));
		
		//-------------------------------------- Code added 15.06 12:06
		VSConnection connect = new VSConnection(socket);
		VSObjectConnection ObjConnect = new VSObjectConnection(connect);
		//-----------------------------------
		
		VSRevMsg revMsg = null;
		VSSenMsg senMsg = null;

		Object[] toSend; // remote: call by refrence
		if (args == null) {
			toSend = new Object[0];
		} else {
			toSend = args.clone();
			for (int i = 0; i < toSend.length; i++) {
				if (toSend[i] instanceof Remote) {
					Remote stub = VSRemoteObjectManager.getInstance().getproxy(
							(Remote) toSend[i]);
					toSend[i] = stub;
				}
			}
		}
		
			//----------------------------------------- Code added 15.06 12:06
			VSBOCThread BOCThread[] = new VSBOCThread[maxNr];
			
			//-----------------------------------------
			
		for (int i = 0; i < maxNr; i++) { 
			try {
				senMsg = new VSSenMsg(remote.getObjectID(),method.toGenericString(), toSend, requestId, i);
								
				t_begin = System.currentTimeMillis();     //begin
				
				//-------------------------------------- Code quoted 15.06 12:06
				//connect.sendObject(senMsg);				//2500
				
				//-------------------------------------- Code added 15.06 12:06
				BOCThread[i]=new VSBOCThread(i,senMsg,connect);
				BOCThread[i].start();
				//--------------------------------------
				
				
				System.out.println("recvbegin = " + t_begin);
				
				// System.out.println("send message ");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("unable to send proxy in invocationhandler involke!");
			}
			
			resttime =  socketTimeout;
			while(resttime > 0) {
				try {
					// antwort empfangen
					socket.setSoTimeout(resttime);
					System.out.println("start receive"); 
					revMsg = (VSRevMsg) ObjConnect.receiveObject();
					
					//test time
					runTime = System.currentTimeMillis()-t_begin ;      //end
					resttime = (int) (socketTimeout-runTime);
					
					//for test
					System.out.println("runTime = " + runTime);     
					System.out.println("resttime is " + resttime);
					
					if (resttime <= 0) {
						socket.setSoTimeout(0);   //redunance
						break;
					}
					// latest antwort comes,return
					if (revMsg == null || !(revMsg.getRequestID() == requestId && revMsg
									.getSequenzNr() == i)) {
						System.out.println("Sequenznumm is " + i);
						continue;
					}
					System.out.println("got right answer for requestID "+ requestId + " at " + i + " SequenzNr");

					// if(revMsg == null){
					// throw new RemoteException("unable to get response ");
					// }
					//
					socket.setSoTimeout(0);
					// System.out.println("+++++++get right response+++++++");
					Throwable exc = revMsg.getFehler();
					if (exc != null) {
						Type[] allowedexceptiontypescollection = method
								.getGenericExceptionTypes();
						for (Type singleallowedexception : allowedexceptiontypescollection) {
							if (((Class<?>) singleallowedexception)
									.isAssignableFrom(exc.getClass())) {
								throw exc;
							}
						}
					}
					return revMsg.getResult();// not latest antwort,receive
												// until timeout and next
												// request
				} catch (SocketTimeoutException e) {
					runTime = System.currentTimeMillis()-t_begin;
					System.out.println("SocketTimeoutException  = " + runTime);
					System.out.println("request" + " " + requestId + " "
							+ "timeout" + " " + i);
					socket.setSoTimeout(0);
					break;
				} catch (Exception e) {
					System.out.println("something wrong" + i);
					e.printStackTrace();
					break;
					// System.out.println(e.getMessage());
				}
			}
		}

		throw new RemoteException("Max retries reached.");
	}
}
