package vsue.communication;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;


public class VSConnection{
	
	private Socket socket;
	
	public VSConnection(Socket socket){
		
		this.socket=socket;
	}
	
	public void sendChunk(byte[] chunk)  {
		
		try {
			OutputStream out=socket.getOutputStream();
			out.write(chunk.length);
			out.write(chunk);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	 
	public byte[] receiveChunk() {
		
		byte[] chunk=null;
		InputStream in=null;
		try {
			in=socket.getInputStream();
			
	       if(in!=null){
	    	  try{ 
	    	   int grosse=in.read();
	    	   if(grosse>=0){
				chunk=new byte[grosse];
				in.read(chunk);
	    	   }
	    	  }catch(SocketException e){
	  			e.printStackTrace();
	  		}
	       }
//		try{	
//			System.out.println("receive Groesse:"+chunk.length);
//		}catch(NullPointerException e){
//			e.printStackTrace();
//		}
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		
	
		return chunk;
	}
}
