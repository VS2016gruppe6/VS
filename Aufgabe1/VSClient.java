package vsue.communication;


import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class VSClient {
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		int port = 1234;
		Socket client= new Socket("127.0.0.1",port);
		boolean flag=true;
		
		while(!client.isBound()){}
		System.out.println("TCP is bound!");
		
		VSObjectConnection connect=new VSObjectConnection(new VSConnection(client));
		
	
		
		Serializable o1=null;
		

		
		/*  int test               */
		int i=5;
		connect.sendObject(i);
		System.out.println("Integer send to Server");
		o1 = connect.receiveObject();
			if (o1 != null){
				System.out.println("Integer received");
				
				if(i==(int)o1){
					System.out.println("Integer korrekt");
				}else{
					System.out.println("Integer falsch");
				}
			}else{
				System.out.println("backreceive null");
			}
				
			
			
		/*  array test               */
			int[] arrayTest=new int[]{1,2,3};
			connect.sendObject(arrayTest);
			System.out.println("Array test");
			
			o1 = connect.receiveObject();
			int[] receiveArray=(int[])o1;
			if (o1 != null){
				System.out.println("Array from Server received");
				
				if(Arrays.equals(receiveArray,arrayTest)){
					System.out.println("Array korrekt");
				}else{
					System.out.println("Array falsch");
					System.out.println("send"+arrayTest);
					System.out.println("aber receive "+receiveArray);
				}
			}else{
				System.out.println("backreceive null");
			}
//			
	
		/*  string test               */
			String stringTest="string test";
			connect.sendObject(stringTest);
			System.out.println("stringTest");
			o1 = connect.receiveObject();
			
			if (o1 != null){
				System.out.println("string from Server received");
				
				if(stringTest.equals(o1)){
					System.out.println("string korrekt");
				}else{
					System.out.println("string falsch");
					System.out.println("send"+stringTest);
					System.out.println("aber receive "+o1);
				}
			}else{
				System.out.println("backreceive null");
			}
			
//			  client.close();
//			    connect=null;
//				client= new Socket("127.0.0.1",port);
//				connect=new VSObjectConnection(new VSConnection(client));
//			
			/*  Auktion test               */
			VSAuction auction=new VSAuction("firstAuction",2,3);
			connect.sendObject(auction);
			System.out.println("Auktion Test");
			o1 = connect.receiveObject();
			VSAuction r_auction=(VSAuction)o1;
			if (o1 != null){
				System.out.println("Auktion from Server received");
				
				if(auction.r_equals(o1)){
					System.out.println("Auktion korrekt");
				}else{
					System.out.println("Auktion falsch");
					System.out.println("send"+" "+"name:"+auction.getName()+" "+"price:");
					System.out.println("aber receive"+" "+"name:"+r_auction.getName()+" "+"price:"+r_auction.getPrice());
				}
			}else{
				System.out.println("backreceive null");
			}
		client.close();
	}	
	
}
