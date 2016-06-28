package vsue.replica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;




public class VSKeyValueClient implements VSKeyValueReplyHandler {
	
	/* The addresses of all potential replicas. */
	private final InetSocketAddress[] replicaAddresses;	
	private VSKeyValueRequestHandler Request_Handler;
	private String GetResult;	
	public VSKeyValueClient(InetSocketAddress[] replicaAddresses) {
		this.replicaAddresses = replicaAddresses;
	}
	
	
	// #############################
	// # INITIALIZATION & SHUTDOWN #
	// #############################

	public void init() {
		// Export client
		try {		
	
//			Registry registry = LocateRegistry.getRegistry("faui02v",12346);
			
			//export und with replica random conncted
			UnicastRemoteObject.exportObject(this, 0);
			int replipcaNr = (int) (Math.random()*2);   //2 : amount of replica
			Registry registry = LocateRegistry.getRegistry(replicaAddresses[replipcaNr].getHostString(),
																	replicaAddresses[replipcaNr].getPort());
			Request_Handler = (VSKeyValueRequestHandler) registry.lookup("service");
			
			System.out.println("connected with replica in "+replicaAddresses[replipcaNr]+"!");		
			
		} catch(RemoteException | NotBoundException re) {
			System.err.println("Unable to export client: " + re);
			System.err.println("The client will not be able to receive replies");
			return;
		}
	}
	
	public void shutdown() {
		// Unexport client
		try {
			UnicastRemoteObject.unexportObject(this, true);
		} catch(NoSuchObjectException nsoe) {
			// Ignore
		}
	}


	// #################
	// # REPLY HANDLER #
	// #################
	
	@Override
	public void handleReply(VSKeyValueReply reply) {
		switch (reply.GetReplyingOperation()){
			case PUT:
				break;
				
			case GET:
			//	System.out.println("Value Readed:   "+reply.GetReplyingValue());
				GetResult = reply.GetReplyingValue();
				break;
				
			case EXISTS:
				if(reply.GetUpdatingTime()!=0){
					SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");  
					System.out.println(sdf.format(new Date(reply.GetUpdatingTime())));
				}
				else
					System.out.println("no matched results found");
				break;
				
			case RELIABLE_EXISTS:
				break;
				
			default:
				break;
				
		}
	}


	// ###################
	// # KEY-VALUE STORE #
	// ###################

	public void put(String key, String value) throws RemoteException {	
		VSKeyValueRequest Request = new VSKeyValueRequest(this,VSKeyValueOperation.PUT,key,value,0);
		Request_Handler.handleRequest(Request);
	}
	
	public String get(String key) throws VSKeyValueException, RemoteException {
		
		VSKeyValueRequest Request = new VSKeyValueRequest(this,VSKeyValueOperation.GET,key,null,0);
		Request_Handler.handleRequest(Request);	
		if (GetResult != null)
			return GetResult;
		else
		return null;
	}

	public void delete(String key) throws RemoteException {
		VSKeyValueRequest Request = new VSKeyValueRequest(this,VSKeyValueOperation.DELETE,key,null,0);
		Request_Handler.handleRequest(Request);
	}

	public long exists(String key) throws RemoteException {
		VSKeyValueRequest Request = new VSKeyValueRequest(this,VSKeyValueOperation.EXISTS,key,null,0);
		Request_Handler.handleRequest(Request);
		return -1L;
	}

	public long reliableExists(String key, int threshold) throws RemoteException {
		VSKeyValueRequest Request = new VSKeyValueRequest(this,VSKeyValueOperation.RELIABLE_EXISTS,key,null,0);
		Request_Handler.handleRequest(Request);
		return -1L;
	}

	
	// #########
	// # SHELL #
	// #########

	public void shell() {
		// Create input reader and process commands
		BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			// Print prompt
			System.out.print("> ");
			System.out.flush();
			
			// Read next line
			String command = null;
			try {
				command = commandLine.readLine();
			} catch(IOException ioe) {
				break;
			}
			if(command == null) break;
			if(command.isEmpty()) continue;
			
			// Prepare command
			String[] args = command.split(" ");
			if(args.length == 0) continue;
			args[0] = args[0].toLowerCase();
			
			// Process command
			try {
				boolean loop = processCommand(args);
				if(!loop) break;
			} catch(Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
		
		// Close input reader
		try {
			commandLine.close();
		} catch(IOException ioe) {
			// Ignore
		}
	}
	
	private boolean processCommand(String[] args) throws VSKeyValueException, RemoteException {
		switch(args[0]) {
		case "put":
		case "p":
			if(args.length < 3) throw new IllegalArgumentException("Usage: put <key> <value>");
			put(args[1], args[2]);
			break;
		case "get":
		case "g":
			if(args.length < 2) throw new IllegalArgumentException("Usage: get <key>");
			String value = get(args[1]);
			System.out.println(value);
			break;
		case "delete":
		case "del":
		case "d":
			if(args.length < 2) throw new IllegalArgumentException("Usage: delete <key>");
			delete(args[1]);
			break;
		case "exists":
		case "e":
			if(args.length < 2) throw new IllegalArgumentException("Usage: exists <key>");
			long timestamp = exists(args[1]);
			System.out.println((timestamp < 0) ? "Key not found" : "Last modified: " + timestamp);
			break;
		case "reliable-exists":
		case "re":
		case "r":
			if(args.length < 3) throw new IllegalArgumentException("Usage: reliable-exists <key> <threshold>");
			int threshold = Integer.parseInt(args[2]);
			timestamp = reliableExists(args[1], threshold);
			System.out.println((timestamp < 0) ? "Key not found" : "Last modified: " + timestamp);
			break;
		case "exit":
		case "quit":
		case "x":
		case "q":
			return false;
		default:
			throw new IllegalArgumentException("Unknown command: " + args[0]);
		}
		return true;
	}

	
	// ########
	// # MAIN #
	// ########
	
	public static void main(String[] args) throws IOException {
		// Check arguments
		if(args.length < 1) {
			System.err.println("usage: java " + VSKeyValueClient.class.getName() + " <path-to-replica-addresses-file>");
			System.exit(1);
		}
		
		// Load replica addresses
		LinkedHashSet<InetSocketAddress> addresses = new LinkedHashSet<InetSocketAddress>();
		List<String> lines = Files.readAllLines(Paths.get(args[0]), Charset.defaultCharset());
		for(String line: lines) {
			// Skip empty lines and comments
			if(line.isEmpty()) continue;
			if(line.startsWith("#")) continue;
			
			// Parse line
			try {
				String[] parts = line.split(":");
				InetSocketAddress address = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
				addresses.add(address);
			} catch(Exception e) {
				System.err.println("Ignore line \"" + line + "\" (" + e + ")");
			}
		}
		InetSocketAddress[] replicaAddresses = addresses.toArray(new InetSocketAddress[addresses.size()]);
				
		// Create and execute client
		VSKeyValueClient client = new VSKeyValueClient(replicaAddresses);
		client.init();
		client.shell();
		client.shutdown();
	}

}
