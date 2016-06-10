package vsue.rpc;


import java.io.IOException;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class VSServer implements Runnable {
		private static Socket _socket = null;
			public VSServer(Socket socket){
				 _socket = socket;
			}
			@Override
			public void run() {
							
				VSObjectConnection connect=new VSObjectConnection(new VSConnection(_socket));
			
				Object o=null;
				VSRemoteObjectManager objectManager = VSRemoteObjectManager.getInstance();
				try {		
						VSSenMsg senMsg;
						senMsg=(VSSenMsg)connect.receiveObject();
						
						if(senMsg==null){
							System.out.println("server doesn't receive");
							System.out.println("server doesn't receive");
						}
						VSRevMsg revMsg;
						try
						{
							o=objectManager.invokeMethod(senMsg.getObjectID(),senMsg.getMethodName(),senMsg.getParameters());
							//set reuqestID and sequenzNr
							revMsg=new VSRevMsg(o,null,senMsg.getRequestID(),senMsg.getSequenzNr());
						}catch(Throwable e)
						{
							revMsg=new VSRevMsg(null,e,senMsg.getRequestID(),senMsg.getSequenzNr());
						}
						connect.sendObject(revMsg);
						}catch(Exception e){
							e.printStackTrace();
						}			        
		     try {
				_socket.close();
			}
		     catch (SocketTimeoutException e){
		    	 System.out.println("server receiver ");
		     }catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("unable to close socket in thread");
				System.err.println(e.getMessage());
			} 
			}

}
