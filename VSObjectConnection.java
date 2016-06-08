package vsue.rpc;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class VSObjectConnection {
	
	private VSConnection connect;
 
	public VSObjectConnection(VSConnection connect ){
		
		this.connect=connect;
	}
	
	public void sendObject(Serializable object){
		
		ByteArrayOutputStream stream_byteArray=null;
		ObjectOutputStream object_stream=null;
		byte[] byteSend = null;
		try {
			stream_byteArray=new ByteArrayOutputStream();
			object_stream = new ObjectOutputStream(stream_byteArray);
			object_stream.writeObject(object);
			object_stream.flush();    //gibt buffer
			
			
			byteSend=stream_byteArray.toByteArray();
			connect.sendChunk(byteSend);
			
			if(object_stream!=null){
				object_stream.close();
				stream_byteArray.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		};

		
	}
	
	public Serializable receiveObject() {
		
		byte[] receive = null;
		ByteArrayInputStream byte_stream=null;
		ObjectInputStream stream_object = null;
		Object re=null;
		Serializable result = null;
		
		try {
			receive = connect.receiveChunk();
			if(receive==null){
				return null;
			}
		
			byte_stream=new ByteArrayInputStream(receive);
		
			stream_object=new ObjectInputStream(byte_stream);

			re=stream_object.readObject();
			result = (Serializable)re;
			
			if(stream_object!=null){
				stream_object.close();
				byte_stream.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return (Serializable)result;
		
	}
}
