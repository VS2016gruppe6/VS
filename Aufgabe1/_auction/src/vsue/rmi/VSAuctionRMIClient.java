package vsue.rmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Provider.Service;

public class VSAuctionRMIClient implements VSAuctionEventHandler {
	/* The user name provided via command line. */
	private final String userName;
	
	private VSAuctionService service;

	public VSAuctionRMIClient(String userName) {
		this.userName = userName;
	}

	// #############################
	// # INITIALIZATION & SHUTDOWN #
	// #############################
	public void init(String registryHost, int registryPort) {
		/*
		 * TODO: Implement client startup code
		 */
		try {
			Registry registry = LocateRegistry.getRegistry(registryHost,
					registryPort);
			service = (VSAuctionService) registry
					.lookup("service");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void shutdown() {
		/*
		 * TODO: Implement client shutdown code
		 */
		System.exit(0);
	}

	// #################
	// # EVENT HANDLER #
	// #################
	@Override
	public void handleEvent(VSAuctionEventType event, VSAuction auction) {
		/*
		 * TODO: Implement event handler
		 */
		
		
		
		
		
	}

	// ##################
	// # CLIENT METHODS #
	// ##################
	public void register(String auctionName, int duration, int startingPrice) {
		/*
		 * TODO: Register auction
		 */
		// VSAuction objekt = new VSAuction(auctionName,startingPrice);
		// VSAuctionService objektService = new
		// VSAuctionService(objekt,duration,);
		
		registerAuction(auctionName,duration,
				VSAuctionEventHandler handler);
		
		getAuctions();
		
		 placeBid(String userName, String auctionName, int price,
					VSAuctionEventHandler handler);
		
		System.out.println("register!");

	}

	public void list() {
		/*
		 * TODO: List all auctions that are currently in progress
		 */
		
		VSAuction[] aktiveVSAuction=getAuctions();
		
		System.out.println("list!");
	}

	public void bid(String auctionName, int price) {
		/*
		 * TODO: Place a new bid
		 */
		System.out.println("bid!");

	}

	// #########
	// # SHELL #
	// #########
	public void shell() {
		// Create input reader and process commands
		BufferedReader commandLine = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			// Print prompt
			System.out.print("> ");
			System.out.flush();

			// Read next line
			String command = null;
			try {
				command = commandLine.readLine();
			} catch (IOException ioe) {
				break;
			}
			if (command == null)
				break;

			if (command.isEmpty())
				continue;

			// Prepare command
			String[] args = command.split(" ");
			if (args.length == 0)
				continue;

			args[0] = args[0].toLowerCase();

			// Process command
			try {
				if (!processCommand(args))
					break;
			} catch (IllegalArgumentException iae) {
				System.err.println(iae.getMessage());
			}
		}

		// Close input reader
		try {
			commandLine.close();
		} catch (IOException ioe) {
			// Ignore
		}
	}

	private boolean processCommand(String[] args) {
		switch (args[0]) {
		case "register":
		case "r":
			if (args.length < 3)
				throw new IllegalArgumentException(
						"Usage: register <auction-name> <duration> [<starting-price>]");
			int duration = Integer.parseInt(args[2]);
			int startingPrice = (args.length > 3) ? Integer.parseInt(args[3])
					: 0;
			register(args[1], duration, startingPrice);
			break;
		case "list":
		case "l":
			list();
			break;
		case "bid":
		case "b":
			if (args.length < 3)
				throw new IllegalArgumentException(
						"Usage: bid <auction-name> <price>");
			int price = Integer.parseInt(args[2]);
			bid(args[1], price);
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
	public static void main(String[] args) {
		// Check arguments
		if (args.length < 3) {
			System.err.println("usage: java "
					+ VSAuctionRMIClient.class.getName()
					+ " <user-name> <registry_host> <registry_port>");
			System.exit(1);
		}

		// Create and execute client
		VSAuctionRMIClient client = new VSAuctionRMIClient(args[0]);
		client.init(args[1], Integer.parseInt(args[2]));
		client.shell();
		client.shutdown();
	}

}
