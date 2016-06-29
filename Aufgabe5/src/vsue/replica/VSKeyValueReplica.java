package vsue.replica;

import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.protocols.SEQUENCER;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.protocols.SEQUENCER.SequencerHeader;


public class VSKeyValueReplica implements VSKeyValueRequestHandler,Receiver{

	
	private Hashtable<String,VSKeyValueStruct> VSKeyValue = new Hashtable<String,VSKeyValueStruct>();
	
	//------------------------------------------code added 28.06
	private JChannel channel;
	private String user_name=System.getProperty("user.name", "n/a");
	private VSKeyValueRequest OnlineRequest = new VSKeyValueRequest();
	private Message Msg = new Message();
	
	
	public void ChannelStart(String ClusterName) throws Exception{
		channel = new JChannel();
		ProtocolStack PS = channel.getProtocolStack();
		PS.addProtocol(new SEQUENCER());
		channel.setReceiver(this);
		//Message Msg = new Message();	
		channel.connect(ClusterName);
		//channel.send(null, "hello");
		//long seqNr = ((SequencerHeader) Msg.getHeader(ClassConfigurator.getProtocolId(SEQUENCER.class))).getSeqno();
		System.out.println("Receiving");
	}
	
	
	

	@Override
	public void handleRequest(VSKeyValueRequest request) throws RemoteException {
		Message Msg = new Message(null,request);
		try {
			channel.send(Msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Forwarding Request to Group:   " + request.toString());
	}
	
	
	public synchronized void ExcuteRequest(VSKeyValueRequest request) throws RemoteException{
	/*	switch (request.GetRequestingOperration()){
		case PUT:
			VSKeyValue.put(request.GetOperationArg1(), new VSKeyValueStruct(request.GetOperationArg2(),System.currentTimeMillis()));
			VSKeyValueReply Reply3 = new VSKeyValueReply(this,
					VSKeyValueOperation.PUT,
					VSKeyValue.get(request.GetOperationArg1()).GetValue(),
					VSKeyValue.get(request.GetOperationArg1()).GetUpdateTime(),
					request.GetRequestID());
			request.GetRequestingClient().handleReply(Reply3);
			break;
		case GET:
			if(VSKeyValue.containsKey(request.GetOperationArg1())){
			VSKeyValueReply Reply1 = new VSKeyValueReply(this,
														VSKeyValueOperation.GET,
														VSKeyValue.get(request.GetOperationArg1()).GetValue(),
														VSKeyValue.get(request.GetOperationArg1()).GetUpdateTime(),
														request.GetRequestID());
			System.out.print(VSKeyValue.get(request.GetOperationArg1()).GetValue());
			request.GetRequestingClient().handleReply(Reply1);
			}
			else
			{
				VSKeyValueReply Reply1 = new VSKeyValueReply(this,
						VSKeyValueOperation.GET,
						null,
						null,
						request.GetRequestID());			
			request.GetRequestingClient().handleReply(Reply1);
			}
			break;
			
		case DELETE:
			VSKeyValue.remove(request.GetOperationArg1());
			VSKeyValueReply Reply4 = new VSKeyValueReply(this,
					VSKeyValueOperation.PUT,
					null,
					null,
					request.GetRequestID());
			request.GetRequestingClient().handleReply(Reply4);
			break;
			
		case EXISTS:
			if(VSKeyValue.containsKey(request.GetOperationArg1())){
				VSKeyValueReply Reply2 = new VSKeyValueReply(this,
															VSKeyValueOperation.EXISTS,
															VSKeyValue.get(request.GetOperationArg1()).GetValue(),
															VSKeyValue.get(request.GetOperationArg1()).GetUpdateTime(),
															request.GetRequestID());
				request.GetRequestingClient().handleReply(Reply2);
			}
			else{
				VSKeyValueReply Reply2 = new VSKeyValueReply(this,
															VSKeyValueOperation.EXISTS,
															null,
															null,
															request.GetRequestID());	
				request.GetRequestingClient().handleReply(Reply2);	
			}
			break;
		
		case RELIABLE_EXISTS:
			break;				
						
		default:
			break;
			
	}*/
	}




	@Override
	public void getState(OutputStream arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void receive(Message arg0) {
		System.out.println("received msg from " + arg0.getSrc() + ": "
				+ arg0.getObject());
		try {
			ExcuteRequest((VSKeyValueRequest)arg0.getObject());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	@Override
	public void setState(InputStream arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void block() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void suspect(Address arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void unblock() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void viewAccepted(View arg0) {
		// TODO Auto-generated method stub
		
	}


}
