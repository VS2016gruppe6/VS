package vsue.rpc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


public class VSInvocationHandler implements InvocationHandler,Serializable {
	
	private VSRemoteReference remote;  ///remoteReference
	
	static final private int maxNr=5;
	
	private int requestId=0;
	private int sequenzNr=0;
	private boolean timeout=false;
	private boolean getResponse=false;// break for schleife
	
	private int socketTimeout=20;
	
	private static Timer timer = new Timer();
	
//	private static class requests{
//		public VSInvocationHandler handler;
//		public Method method;//	public boolean sameRequest(requests a ,requests b){
//	if(a.handler.equals(b.handler)&&Arrays.equals(a.args, b.args)&&a.method.equals(b.method)){
//	return true;
//}
//return false;
//}
//
////same request exists,return requestID,otherwise return -1
//public int getRuestID(Object proxy, Method method, Object[] args){
//requests tempRequest=new requests(this,method,args,0);
//
//	for(requests e:requestTable){
//		if(sameRequest(e,tempRequest)){
//			return e.requestId;
//		}
//	}
//	
//return -1;
//}
//		public Object[] args;
//	    public int requestId;
//	  //  public int sequenzNr;
//		
//		public requests( VSInvocationHandler handler,Method method,Object[] args,int requestID){
//			this.handler=handler; TimerTask(){
//			this.method=method;
//			this.args=args;
//			this.requestId=requestID;
//		//	this.sequenzNr=sequenzNr;
//		}
//	}
	
	//store all requests,userd to determine if it is the same request 
//	ArrayList<requests> requestTable = new ArrayList<>();
	
	//timer 
	//Timer timer = new Timer();
	
	public VSInvocationHandler(VSRemoteReference remote) {
	
		this.remote = remote;
				
	}
	
	//compare two requests if they are the same one
//	public boolean sameRequest(requests a ,requests b){
//		if(a.hangetResponsedler.equals(b.handler)&&Arrays.equals(a.args, b.args)&&a.method.equals(b.method)){
//			return true;
//		}
//		return false;
//	}
//	
//	//same request exists,return requestID,otherwise return -1
//	public int getRuestID(Object proxy, Method method, Object[] args){
//		requests tempRequest=new requests(this,method,args,0);
//	
//			for(requests e:requestTable){
//				if(sameRequest(e,tempRequest)){
//					return e.requestId;
//				}
//			}
//			
//		return -1;
//	}
	
	//fuell alle lokalen aufrufe am Proxy
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	
		//every time requestId add 1
		requestId++;
		
		Socket socket=null;
		VSObjectConnection connect;
		VSRevMsg revMsg=null;
		VSSenMsg senMsg=null;	
			
		socket=new Socket(remote.getHost(),remote.getPort());
		Object[] toSend ;				
		
		if(args == null)
		{
			toSend = new Object[0];
		}else
		{
		 toSend = args.clone();
		for(int i=0;i<toSend.length;i++)
		{
			if(toSend[i] instanceof Remote)
			{
				Remote stub = VSRemoteObjectManager.getInstance().getproxy((Remote)toSend[i]);
					toSend[i]=stub;
			}
		}
		}
		
		connect=new VSObjectConnection(new VSConnection(socket));	
		
		//timeout schedule 50ms
	
			//maximal 5 times send and receive
		for (int i = 0; i < maxNr; i++) {
			//every time timeout should be inited to false
			timeout=false;
			try {
				senMsg = new VSSenMsg(remote.getObjectID(),method.toGenericString(), toSend, requestId++,i);
				connect.sendObject(senMsg);
				System.out.println("send message ");
			} catch (Exception e) {
				System.out.println("unable to send proxy in invocationhandler involke!");
			}
				//System.out.println("timeout ist "+timeout);

				int runouttime = 0;
			while (!timeout) {
				try {
					// antwort empfangen
					socket.setSoTimeout(socketTimeout- runouttime);
					revMsg = (VSRevMsg) connect.receiveObject();
					runouttime = (int)System.currentTimeMillis();
//					if (revMsg == null){
//						//System.out.println("client receive ist null");
//						}else{
//							System.out.println("client receive ist not null");
//					// antwort comes before timeout
//					// latest antwort comes,return
					if (revMsg.getRequestID() == this.requestId && revMsg.getSequenzNr() == i) {	
								timeout = true;
								System.out.println("+++++++get right response+++++++");
						Throwable exc = revMsg.getFehler();
						if (exc != null) {
							Type[] allowedexceptiontypescollection = method
									.getGenericExceptionTypes();
							for (Type singleallowedexception : allowedexceptiontypescollection) {
								if (((Class<?>) singleallowedexception).isAssignableFrom(exc.getClass())) {
									throw exc;
								}
							}
						}
						return revMsg.getResult();
					} 
					// not latest antwort,receive until timeout and next request
				} catch (SocketTimeoutException e) {
					System.out.println("request"+" "+requestId+" "+"timeout"+" "+i);			
					continue;
				}catch(Exception e){
					System.out.println("something wrong"+i);
				}
			}
			System.out.println("ausser while schleife ");
		}
		 System.out.println("can run before revMsg.getResult()");
		return revMsg.getResult();

		//throw new RemoteException("no answer get ");
	}
}
