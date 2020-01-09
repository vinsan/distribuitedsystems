package doomdragon.distribuitedsystems;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

public class AuctionPeer implements AuctionMechanism {
	final private PeerDHT peer;
	private String username;
	final private int DEFAULT_MASTER_PORT=4000;
	int id;

	public AuctionPeer(int peerId, String username, String address) throws Exception {
		peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(peerId)).ports(DEFAULT_MASTER_PORT+peerId).start()).start();
		FutureBootstrap fb = this.peer.peer().bootstrap().inetAddress(InetAddress.getByName(address)).ports(DEFAULT_MASTER_PORT).start();
		fb.awaitUninterruptibly();
		if(fb.isSuccess()) {
			peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
		}
		this.username = username;
		this.id = peerId;
	}

	public boolean createAuction(String _auction_name, Date _end_time, double _reserved_price, String _description) {
		if(_reserved_price<0.0)	//can't have a reserved price less than 0, if RP is 0 it means that the seller accepts any offer
			return false;
		long millis=System.currentTimeMillis();
		Date currentdate=new java.util.Date(millis);
		if(_end_time.before(currentdate))	//can't open an already expired auction
			return false;
		Auction auction = new Auction(_auction_name, _end_time, _reserved_price, _description, username);
		try {
			store(_auction_name, auction);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void store(String name, Auction au) throws IOException {
		peer.put(Number160.createHash(name)).data(new Data(au)).start().awaitUninterruptibly();
	}

	public String checkAuction(String _auction_name) {
		try {
			Auction au = get(_auction_name);
			if(au!=null)
				return au.checkAuction();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
		}
		return "not found";
	}

	public Auction get(String name) throws ClassNotFoundException, IOException {
		FutureGet futureGet = peer.get(Number160.createHash(name)).start();
		futureGet.awaitUninterruptibly();
		if(futureGet.isSuccess()) {
			return (Auction) futureGet.dataMap().values().iterator().next().object();
		}
		return null;
	}

	public String placeAbid(String _auction_name, double _bid_amount) {
		String status = checkAuction(_auction_name);
		if(status.equals("open")) {
			try {
				Auction auction = get(_auction_name);
				if(auction!=null) {
					status = auction.bid(username, _bid_amount);
					if(status.equals("open"))
						peer.put(Number160.createHash(_auction_name)).data(new Data(auction)).start().awaitUninterruptibly();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return status;
	}

	public void printAllAuction() throws ClassNotFoundException, IOException {
		Iterator<Number640> keySet = peer.storageLayer().get().keySet().iterator();
		if(!keySet.hasNext()) {
			System.out.println("No auctions!");
			return;
		}
		while(keySet.hasNext()) {
			Number640 key = keySet.next();
			Auction auction = (Auction) peer.storageLayer().get(key).object();
			System.out.println("Object: "+auction.toString());
		}
	}

	public String getUsername(){
		return username;
	}

	public String cancelAuction(String auctionName) throws IOException, ClassNotFoundException {
		try {
			Auction au = get(auctionName);
			if(au.sellersName.equals(username)) {
				if(au.listOfBid.size()==0) {
					peer.remove(Number160.createHash(auctionName)).start().awaitUninterruptibly().isSuccess();
					return "Auction successfully removed";
				} else return "Someone made bids for this auction. You can't delete it";
			}else return "You cannot cancel another user's auction";
		} catch (NoSuchElementException e) {
			return "auction "+auctionName+" not found!";
		}
	}

	public String setReservePrice(String _auction_Name, double newPrice) throws ClassNotFoundException, IOException {
		if(newPrice<0.0)
			return "You can't have a negative reserve price!";
		else {
			try {
				Auction au = get(_auction_Name);
				if(!au.checkAuction().equals("open"))
					return _auction_Name+" is already closed!";
				else if(au.sellersName.equals(username)) {
					au.reservedPrice = newPrice;
					peer.put(Number160.createHash(_auction_Name)).data(new Data(au)).start().awaitUninterruptibly();
					return "Reserve price for auction "+_auction_Name+" set to "+newPrice;
				}else return "You cannot change another user's auction's reserve price!";
			} catch (NoSuchElementException e) {
				return "auction "+_auction_Name+" not found!";
			}
		}
	}

	public String changeEndTime(String _auction_Name, Date _new_end_time) throws ClassNotFoundException, IOException {
		long millis=System.currentTimeMillis();
		Date currentdate=new java.util.Date(millis);
		if(_new_end_time.before(currentdate))
			return "New End Time is before current date!";
		else {
			try {
				Auction au = get(_auction_Name);
				if(!au.sellersName.equals(username))
					return "You cannot change the end date of another user's auction!";
				else if(!au.checkAuction().equals("open")) {
					if(au.listOfBid.size()==0||au.checkAuction().contains("Auction was closed with no winner")) {
						au.endTime = _new_end_time;
						peer.put(Number160.createHash(_auction_Name)).data(new Data(au)).start().awaitUninterruptibly();
						return "The auction for "+_auction_Name+" has been reopened";
					} else return "You can't re-open an auction for a sold item!";
				}else{
					if(au.listOfBid.size()==0) {
						au.endTime = _new_end_time;
						peer.put(Number160.createHash(_auction_Name)).data(new Data(au)).start().awaitUninterruptibly();
						return "The auction for "+_auction_Name+" will be closed on "+_new_end_time;
					}else return "You cannot change the end date for an auction with offers!";
				}
			}catch (NoSuchElementException e) {
				return "auction "+_auction_Name+" not found!";
			}
		}
	}

	public void shutdown() throws ClassNotFoundException, IOException {
		peer.shutdown();
	}

}