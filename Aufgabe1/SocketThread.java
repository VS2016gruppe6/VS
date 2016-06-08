package vsue.communication;


import java.io.Serializable;
import java.net.Socket;

public class SocketThread extends Thread {
	
	private Socket socket;
	
	public SocketThread(Socket socket){
		
		this.socket=socket;
		
		System.out.println("neu Thread created!");
	}
	
	public void run() {
		
		VSObjectConnection connect=new VSObjectConnection(new VSConnection(socket));
		Serializable se=null;
		
		do {
			try {
				//System.out.println("receive from client? ");
				
					se=connect.receiveObject();
					if(se==null){
						break;
					}
					System.out.println("receive from client "+se);
					
					if(se!=null){
			
					System.out.println("receive from client not null");
					//System.out.println("Object i send test  :"+se);
					
					connect.sendObject(se);
					System.out.println("Object i  backsend");
					}

				}catch(Exception e){
					e.printStackTrace();
				}
		}while(true);
		
	}
}
