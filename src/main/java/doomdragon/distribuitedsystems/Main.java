package doomdragon.distribuitedsystems;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws NumberFormatException, Exception {
		AuctionPeer dns = login();
		Scanner input = new Scanner(System.in);
		boolean loop = true;
		String ans = "";
		while(loop) {
			printMenu();
			ans = input.next();
			loop = exec(ans, input, dns);
		}
		logout(dns, input);
	}

	public static AuctionPeer login() throws Exception {
		String name = System.getenv("NAME");
		int id;
		String address = System.getenv("HOSTNAME");
		if(name==null)
			name = "default";
		if(address==null)
			address = "127.0.0.1";
		try {
			id = Integer.parseInt(System.getenv("ID"));
		}catch(Exception e){
			id = 0;
		}
		System.out.println("Starting Peer with ID: "+id+", Name: "+name+" Address: "+address);
		AuctionPeer dns = new AuctionPeer(id, name, address);
		System.out.println("Welcome "+dns.getUsername()+"!");
		return dns;
	}

	public static void printMenu() {
		System.out.println("Enter CLOSE or EXIT to exit.\nEnter NEW to create a new Auction.\n"
				+ "Enter STATUS to check the Status of an Auction.\nEnter BID to Bid.\n"
				+ "Enter ALL to print all Auctions.\n"+ "Enter CANCEL to cancel an Auction.\n"
				+ "Enter PRICE to change the Reserve Price of an Auction.\n"
				+ "Enter TIME to change the End Time of an Auction.\n");
	}

	public static boolean exec(String ans, Scanner input, AuctionPeer dns) throws ClassNotFoundException, IOException {
		String message = "";
		if(ans.equalsIgnoreCase("close")||ans.equalsIgnoreCase("exit"))
			return false;
		else if(ans.equalsIgnoreCase("new")) {
			System.out.println("Enter the name of the item you're selling: ");
			String _auction_name = input.next();
			System.out.println("Enter the description of the item you're selling: ");
			String _description = input.next();
			System.out.println("Enter the reserved price of the item you're selling: ");
			double _reserved_price = input.nextDouble();
			System.out.println("Enter the Auction End Date. Enter the year: ");
			int year = input.nextInt();
			System.out.println("Enter the Auction End Date. Enter the month. 0 is for January and 11 for December: ");
			int month = input.nextInt();
			System.out.println("Enter the Auction End Date. Enter the Day: ");
			int date = input.nextInt();
			System.out.println("Enter the Auction End Date. Enter the hour: ");
			int hourOfDay = input.nextInt();
			System.out.println("Enter the Auction End Date. Enter the minutes: ");
			int minute = input.nextInt();
			message = create(_auction_name, dns, _reserved_price, _description, year, month, date, hourOfDay, minute);
		}else if(ans.equalsIgnoreCase("status")) {
			System.out.println("Enter the name of the Auction you want to check: ");
			String _auction_name = input.next();
			message = check(dns, _auction_name);
		}else if(ans.equalsIgnoreCase("bid")){
			System.out.println("Enter the name of the Auction you want to bid for: ");
			String _auction_name = input.next();
			System.out.println("Enter the bid amount: ");
			Double _bid_amount = input.nextDouble();
			message = bid(dns, _auction_name, _bid_amount);
		}else if(ans.equalsIgnoreCase("ALL")) {
			dns.printAllAuction();
			return true;
		}else if(ans.equalsIgnoreCase("cancel")) {
			System.out.println("Enter the name of the Auction you want to cancel: ");
			String auctionName = input.next();
			message = cancel(dns, auctionName);
		}else if(ans.equalsIgnoreCase("price")) {
			System.out.println("Enter the name of the Auction you want to modify: ");
			String auctionName = input.next();
			System.out.println("Enter the new Reserve Price amount: ");
			Double newPrice = input.nextDouble();
			message = changePrice(dns, auctionName, newPrice);
		}else if(ans.equalsIgnoreCase("time")) {
			System.out.println("Enter the name of the Auction you want to modify: ");
			String _auction_name = input.next();
			System.out.println("Enter the new Auction End Date. Enter the new year: ");
			int year = input.nextInt();
			System.out.println("Enter the new Auction End Date. Enter the new month. 0 is for January and 11 for December: ");
			int month = input.nextInt();
			System.out.println("Enter the new Auction End Date. Enter the new Day: ");
			int date = input.nextInt();
			System.out.println("Enter the new Auction End Date. Enter the new hour: ");
			int hourOfDay = input.nextInt();
			System.out.println("Enter the new Auction End Date. Enter the new minutes: ");
			int minute = input.nextInt();
			message = changeDate(dns, _auction_name, year, month, date, hourOfDay, minute);
		}
		else message = ans+" isn't a valid command!";
		System.out.println(message);
		return true;
	}

	public static String create(String _auction_name, AuctionPeer dns, Double _reserved_price, String _description, int year, int month, int date, int hourOfDay, int minute) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(year, month, date, hourOfDay, minute);
		System.out.println("End Date is: "+calendar.getTime().toString());
		Boolean response = dns.createAuction(_auction_name, calendar.getTime(), _reserved_price, _description);
		if(response) return "The Auction has been created!";
		else return "There was an error in the Auction creation!";
	}

	public static String check(AuctionPeer dns, String _auction_name) {
		return "Status for the auction for "+_auction_name+" is: "+dns.checkAuction(_auction_name);
	}

	public static String bid(AuctionPeer dns, String _auction_name, Double _bid_amount) {
		String status = dns.placeAbid(_auction_name, _bid_amount);
		if(status.equals("open"))
			return "Auction is "+status+". Bid was successfull.";
		else return "System returned "+status+" as auction status. You didn't bid for this auction.";
	}

	public static String cancel(AuctionPeer dns, String auctionName) throws ClassNotFoundException, IOException {
		return dns.cancelAuction(auctionName);
	}

	public static String changePrice(AuctionPeer dns, String auctionName, Double newPrice) throws ClassNotFoundException, IOException {
		return dns.setReservePrice(auctionName, newPrice);
	}

	public static String changeDate(AuctionPeer dns, String _auction_name, int year, int month, int date, int hourOfDay, int minute) throws ClassNotFoundException, IOException {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(year, month, date, hourOfDay, minute);
		return dns.changeEndTime(_auction_name, calendar.getTime());
	}

	public static void logout(AuctionPeer dns, Scanner input) throws ClassNotFoundException, IOException {
		System.out.println(dns.getUsername()+" you just logged out.");
		dns.shutdown();
		input.close();
	}

}
