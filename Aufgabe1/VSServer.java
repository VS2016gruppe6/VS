package vsue.communication;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class VSServer {
	
	public static void main(String[] args) throws IOException {
		
		int port = 1234;
		ServerSocket server= new ServerSocket(port);
		
		Socket socket = null;
		boolean flag = true; ;
		
		while(flag){
			socket=server.accept();
		
			new SocketThread(socket).start();
			
//			 if(socket.isClosed()){  
//		            
//			     socket.close(); 
//			     
//			     server= new ServerSocket(port); 
//			 }  
		}
		
//		server.close();
	}

}
