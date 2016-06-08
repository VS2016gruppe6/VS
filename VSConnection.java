package vsue.rpc;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
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
			int grosse=chunk.length;
			//while(grosse>0){
			//low digits first
			for(int j=0;j<4;j++){
				out.write(grosse);
				grosse=grosse>>8;
			//}
			}
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
	    	   int grosse=(in.read())|(in.read()<<8)|(in.read()<<16)|(in.read()<<24);
	    	   if(grosse>=0){
				chunk=new byte[grosse];
				in.read(chunk);              //grosse datenmenge?
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
