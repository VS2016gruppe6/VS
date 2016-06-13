package vsue.rpc;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


public class VSConnection{
	
	private Socket socket;
	private OutputStream out = null;
	private InputStream in = null;
	
	
	public VSConnection(Socket socket){
	
		this.socket=socket;
		try {
			out=socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			in=socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendChunk(byte[] chunk)  {
		
		try {
			//OutputStream out=socket.getOutputStream();
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
	 
	public byte[] receiveChunk() throws IOException,SocketTimeoutException{
		
		byte[] chunk=null;	
	       if(in!=null){ 
	    	   int grosse=(in.read())|(in.read()<<8)|(in.read()<<16)|(in.read()<<24);
	    	   if(grosse>=0){
				chunk=new byte[grosse];
				in.read(chunk);              //grosse datenmenge?
	    	   }
	       }
//	       if (chunk == null)
//	       System.out.println("null");

		return chunk;
	}
}
