package vsue.rpc;

import java.io.Serializable;
import java.rmi.Remote;

public class VSRemoteReference implements Serializable,Remote{
	
	private String host;
	private int port;
	private int objectID;
	
	VSRemoteReference(String host,int port,int objectID){
		this.host=host;
		this.port=port;
		this.objectID=objectID;
	}
	
	public void setHost(String host){
		this.host=host;
	}
	
	public void setPort(int port){
		this.port=port;
	}
	
	public void setObjectID(int objectID){
		this.objectID=objectID;
	}
	
	public String getHost(){
		
		return host;
	}
	
    public int getPort(){
		
		return port;
	}
    
    public int getObjectID(){
		
		return objectID;
	}
}