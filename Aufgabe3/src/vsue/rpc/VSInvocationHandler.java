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
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// every time requestId add 1
		int runouttime = 0;
		long recvbegin = 0;
		long recvend = 0;
		int resttime = socketTimeout; 
		
		// init
		requestId++;
		Socket socket = null;
		socket = new Socket(remote.getHost(), remote.getPort()); // socker init
		VSBuggyObjectConnection connect = new VSBuggyObjectConnection(
				new VSConnection(socket));
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

		for (int i = 0; i < maxNr; i++) { 
			try {
				senMsg = new VSSenMsg(remote.getObjectID(),method.toGenericString(), toSend, requestId, i);
				connect.sendObject(senMsg);
				// System.out.println("send message ");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("unable to send proxy in invocationhandler involke!");
			}
			runouttime = 0;
			recvbegin = 0;
			recvend = 0;
			
			while(resttime > 0) {
				try {
					// antwort empfangen
					socket.setSoTimeout(resttime); 
					
					//receieve object, get duration time
					recvbegin = System.currentTimeMillis();     //begin
					System.out.println("recvbegin = " + recvbegin);
					revMsg = (VSRevMsg) connect.receiveObject();
					recvend = System.currentTimeMillis();      //end
					System.out.println("recvend = " + recvend);    
					
					runouttime = (int) (recvend - recvbegin);  
					System.out.println("runouttime = " + runouttime);
					resttime -= runouttime;
					System.out.println("resttime is " + resttime);
					if (resttime <= 0) {
						socket.setSoTimeout(0);   //socket close?
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
					recvend = System.currentTimeMillis();
					runouttime = (int) (recvend - recvbegin);
					System.out.println("runouttime = " + runouttime);
					System.out.println("request" + " " + requestId + " "
							+ "timeout" + " " + i);
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
