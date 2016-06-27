package vsue.replica;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
//----------------------------------------------code added 21.06
import org.jgroups.util.RspList;

public class VSKeyValueReplica extends ReceiverAdapter implements VSKeyValueRequestHandler {
		String RepId;
		String RepAdd;
		static VSKeyValueRequest _request ;
		VSKeyValueReply _reply;
		long time;
		String value;
		VSKeyValueReplyHandler client;
		private Hashtable<String, String> VSKeyValue = new Hashtable<String, String>();
		Registry registry = null;
		static JChannel channel = null;
		
		//hashtable to store requests from replica
		private Hashtable<VSKeyValueRequest, Integer> RequestsBuffer = new Hashtable<VSKeyValueRequest,Integer>();
		
		
	//export this replicate
	public VSKeyValueReplica(String ID, String replicaAddresses){
		this.RepId = ID;
		this.RepAdd = replicaAddresses;
		
	}
	// export this replicate
	public void init(){
		try {
			UnicastRemoteObject.exportObject(this, 0);
			
		} catch(RemoteException re) {
			System.err.println("Unable to export client: " + re);
			System.err.println("The client will not be able to receive replies");
		}
		
		try {
			registry = LocateRegistry.createRegistry(12345);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		try {
			registry.bind(RepId, this);
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// handle request from client
	@Override
	public void handleRequest(VSKeyValueRequest request) throws RemoteException {
		// TODO Auto-generated method stub
		 _request = request;
		 start();
		 client = (VSKeyValueReplyHandler) _request.getClientStub();
			
			
		// decide which action to do
		 if(_request.getvalue() != null){
			 System.out.println("put");
			 VSKeyValue.put(request.getkey(),request.getvalue()); // put
			  time = System.currentTimeMillis();
		 }
		 
		 else if(_request.getrequest() == 1){
			 
			 System.out.println("get");
			 value = VSKeyValue.get(request.getkey()); // get
			 _reply = new VSKeyValueReply(value);
			 try {
				 client.handleReply(_reply);
			} catch (VSKeyValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		 }
		 else if(_request.getrequest() == 2){
			 System.out.println("remove");
			 VSKeyValue.remove(request.getkey()); // delete
		 }
		 else if(_request.getrequest() == 3){
			 System.out.println("exist");
			 value = VSKeyValue.get(request.getkey()); // exist
			if(value != null){
				_reply = new VSKeyValueReply(time, value);
				try {
					client.handleReply(_reply);
				} catch (VSKeyValueException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				_reply = new VSKeyValueReply(-1);
				try {
				 client.handleReply(_reply);
				} catch (VSKeyValueException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		 }
	}
	
	public static void start(){
		
		//jgroups join in a group called "ChatCluster" 
		if (channel != null) throw new IllegalStateException("cluster member already started: " + channel);

		try {
			channel = new JChannel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		try {
			channel.connect("gruppe6");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMessage();
		channel.close();	
	}
	// send message
	public static void sendMessage(){
		
		Message msg = new Message(null,null, _request);
		try {
			channel.send(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// receive message
	public void receiveMessage(Message msg) {
		
	
		ReceiverAdapter receiver = new ReceiverAdapter();
		channel.setReceiver(receiver);
		RequestsBuffer.put((VSKeyValueRequest)msg.getObject(),0);
	}
	public static void main(String[] args) throws IOException{
		VSKeyValueReplica replica = new VSKeyValueReplica(args[0],args[1]);
		replica.init();
		start();
		
	
//		disp=new RpcDispatcher(channel, this);	
//		public void getState(Address target, long timeout) throws Exception{			
//		}
  	    
		System.out.println("replica start successfully");
	}
}
