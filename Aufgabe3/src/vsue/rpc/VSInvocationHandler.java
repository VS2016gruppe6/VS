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

	static final private int maxNr = 10; // send Nr.
	private int requestId = 0;
	private int socketTimeout = 500;

	// private static class requests{
	// public VSInvocationHandler handler;
	// public Method method;// public boolean sameRequest(requests a ,requests
	// b){
	// if(a.handler.equals(b.handler)&&Arrays.equals(a.args,
	// b.args)&&a.method.equals(b.method)){
	// return true;
	// }
	// return false;
	// }
	//
	// // same request exists,return requestID,otherwise return -1
	// public int getRuestID(Object proxy, Method method, Object[] args){
	// requests tempRequest=new requests(this,method,args,0);
	//
	// for(requests e:requestTable){
	// if(sameRequest(e,tempRequest)){
	// return e.requestId;
	// }
	// }
	//
	// return -1;
	// }
	// public Object[] args;
	// public int requestId;
	// // public int sequenzNr;
	//
	// public requests( VSInvocationHandler handler,Method method,Object[]
	// args,int requestID){
	// this.handler=handler; TimerTask(){
	// this.method=method;
	// this.args=args;
	// this.requestId=requestID;
	// // this.sequenzNr=sequenzNr;
	// }
	// }

	// store all requests,userd to determine if it is the same request
	// ArrayList<requests> requestTable = new ArrayList<>();

	// timer
	// Timer timer = new Timer();

	public VSInvocationHandler(VSRemoteReference remote) {
		this.remote = remote;
	}

	// compare two requests if they are the same one
	// public boolean sameRequest(requests a ,requests b){
	// if(a.hangetResponsedler.equals(b.handler)&&Arrays.equals(a.args,
	// b.args)&&a.method.equals(b.method)){
	// return true;
	// }
	// return false;
	// }
	//
	// //same request exists,return requestID,otherwise return -1
	// public int getRuestID(Object proxy, Method method, Object[] args){
	// requests tempRequest=new requests(this,method,args,0);
	//
	// for(requests e:requestTable){
	// if(sameRequest(e,tempRequest)){
	// return e.requestId;
	// }
	// }
	//
	// return -1;
	// }

	// fuell alle lokalen aufrufe am Proxy
	@SuppressWarnings("resource")
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// every time requestId add 1
		requestId++;
		// init
		Socket socket = null;
		socket = new Socket(remote.getHost(), remote.getPort()); // socket init
		VSBuggyObjectConnection connect = new VSBuggyObjectConnection(
				new VSConnection(socket)); // buggy delay
		VSRevMsg revMsg = null;
		VSSenMsg senMsg = null;

		// send all object
		Object[] toSend;
		if (args == null) {
			toSend = new Object[0];
		} else {
			toSend = args.clone(); // why clone?
			for (int i = 0; i < toSend.length; i++) {
				if (toSend[i] instanceof Remote) {
					Remote stub = VSRemoteObjectManager.getInstance().getproxy(
							(Remote) toSend[i]);
					toSend[i] = stub;
				}
			}
		}

		//send process
		A: for (int i = 0; i < maxNr; i++) { // continue --A
			try {
				senMsg = new VSSenMsg(remote.getObjectID(),
						method.toGenericString(), toSend, requestId, i);
				connect.sendObject(senMsg);
				// System.out.println("send message ");
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("unable to send proxy in invocationhandler involke!");
			}

			int runouttime = 0; // ? reset to 0?
			long recvbegin = 0;
			long recvend = 0;

			for (int resttime = socketTimeout - runouttime; resttime > 0;) { // logic?
				try {
					// antwort empfangen
					socket.setSoTimeout(resttime);
					recvbegin = System.currentTimeMillis(); // get now time
					// System.out.println("recvbegin = " + recvbegin);
					revMsg = (VSRevMsg) connect.receiveObject();
					recvend = System.currentTimeMillis();
					
					// System.out.println("recvend = " + recvend);
					runouttime = (int) (recvend - recvbegin);
					System.out.println("runouttime = " + runouttime);
					resttime -= runouttime;
					System.out.println("resttime is " + resttime);
					
					if (resttime <= 0) {   //timeout
						continue A;
					}
					// // latest antwort comes,return
					if (!(revMsg.getRequestID() == requestId && revMsg
							.getSequenzNr() == i)) {
						continue;
					}
					System.out.println("get right answer for requestID "
							+ requestId + " at " + i + " SequenzNr");
					break;
				} catch (SocketTimeoutException e) {
					recvend = System.currentTimeMillis();
					System.out.println("sockettimeout at " + recvend);
					runouttime = (int) (recvend - recvbegin);
					System.out.println("runouttime = " + runouttime);
					resttime -= runouttime;
					System.out.println("resttime is " + resttime);
					if (resttime <= 0) {
						continue A;
					}
					System.out.println("request" + " " + requestId + " "
							+ "timeout" + " " + i);
					continue;
				} catch (Exception e) {
					System.out.println("something wrong" + i);
					System.out.println(e.getMessage());
				}
			}
			break;
		}
		
		
		
		if (revMsg == null) {
			throw new RemoteException("unable to get response ");
		}
		// System.out.println("+++++++get right response+++++++");
		Throwable exc = revMsg.getFehler();
		if (exc != null) {
			Type[] allowedexceptiontypescollection = method.getGenericExceptionTypes();
			for (Type singleallowedexception : allowedexceptiontypescollection) {
				if (((Class<?>) singleallowedexception).isAssignableFrom(exc.getClass())) {  //?
					throw exc;
				}
			}
		}
		return revMsg.getResult();// not latest antwort,receive until timeout
									// and next request
	}
}
