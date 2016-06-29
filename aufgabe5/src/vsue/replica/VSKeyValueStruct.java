package vsue.replica;

public class VSKeyValueStruct {
	
	private String value;
	
	private long UpdateTime;
	
	public VSKeyValueStruct(String value,long time){
		this.value = value;
		this.UpdateTime = time;
	}
	
	public void SetValue(String value){
		this.value = value;
	}
	
	public void SetUpdateTime(long time){
		this.UpdateTime = time;
	}
	
	public String GetValue(){
		return this.value;
	}
	
	public long GetUpdateTime(){
		return this.UpdateTime;
	}

}
