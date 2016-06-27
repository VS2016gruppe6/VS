package vsue.replica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedHashSet;
import java.util.List;




public class VSKeyValueClient implements VSKeyValueReplyHandler {
	
	/* The addresses of all potential replicas. */
	private final InetSocketAddress[] replicaAddresses;
	VSKeyValueRequest request ;
	VSKeyValueRequestHandler replica;
	VSKeyValueReply _reply;
	Registry registry;
	VSKeyValueReplyHandler Sc ;
	String value;
	int error;
	long time;
	public VSKeyValueClient(InetSocketAddress[] replicaAddresses) {
		this.replicaAddresses = replicaAddresses;
	}
	
	
	// #############################
	// # INITIALIZATION & SHUTDOWN #
	// #############################

	public void init() {
		// Export client
	
		try {
			  Sc = (VSKeyValueReplyHandler) UnicastRemoteObject.exportObject(this, 0);
			
		} catch(RemoteException re) {
			System.err.println("Unable to export client: " + re);
			System.err.println("The client will not be able to receive replies");
		}


		// lookup stub of replica
		try {
			registry = LocateRegistry.getRegistry(12345);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		
		String RepId = "0";// todo: lookup by id
		try {
			//replica = (VSKeyValueReplica) registry.lookup(RepId);
			replica = (VSKeyValueRequestHandler) registry.lookup(RepId);
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void handleReply(VSKeyValueReply reply) throws VSKeyValueException {
		/*
		 * TODO: Handle incoming replies sent by replicas
		 */
		//System.out.println("111111111");
		_reply = reply;
		if(_reply.geterror() == -1){
			error = _reply.geterror();
			
		}
//		if(_reply.getvalue() == null){
//			throw new VSKeyValueException("value of this key not exist");
//		}
		else if(_reply.gettime() !=0){
			//System.out.println("exist");
			time = _reply.gettime();
			value = _reply.getvalue();

		}
		else{
			
			value = _reply.getvalue();

		}
	}


	// ###################
	// # KEY-VALUE STORE #
	// ###################

	public void put(String key, String value) throws RemoteException {
		/*
		 * TODO: Invoke PUT operation
		 */
		
		request = new VSKeyValueRequest(key,Sc,value);
		replica.handleRequest(request);
	}
	
	public String get(String key) throws VSKeyValueException, RemoteException {
		/*
		 * TODO: Invoke GET operation
		 */
		//todo  not exist exception
		request = new VSKeyValueRequest(key,Sc,1);
		replica.handleRequest(request);
		if(value == null){
			throw new VSKeyValueException("error wenn call exist");
		}
		return value;
		
	}

	public void delete(String key) throws RemoteException {
		/*
		 * TODO: Invoke DELETE operation
		 */
		request = new VSKeyValueRequest(key,Sc,2);
		replica.handleRequest(request);
	}

	public long exists(String key) throws RemoteException {
		/*
		 * TODO: Invoke EXISTS operation
		 */
		request = new VSKeyValueRequest(key,Sc,3);
		replica.handleRequest(request);
		if(error == -1){
			return -1L;
		}
		return time;
	}

	public long reliableExists(String key, int threshold) throws RemoteException {
		/*
		 * TODO: Invoke reliable EXISTS operation (Exercise 5.3, optional for 5.0 ECTS)
		 */
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
