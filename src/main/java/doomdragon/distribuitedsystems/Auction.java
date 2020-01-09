package doomdragon.distribuitedsystems;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Auction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4015819678297072648L;
	String name;
	Date endTime;
	double reservedPrice;
	String description;
	String sellersName;
	ArrayList<Bid> listOfBid;

	public Auction(String _auction_name, Date _end_time, double _reserved_price, String _description, String sellersName) {
		name = _auction_name;
		endTime = _end_time;
		reservedPrice = _reserved_price;
		description = _description;
		this.sellersName = sellersName;
		listOfBid = new ArrayList<Bid>();
	}

	public String checkAuction(){
		long millis=System.currentTimeMillis();
		Date currentdate=new java.util.Date(millis);
		if(endTime.before(currentdate)) {	//if the end time is before the current date the auction is over
			return declareWinner();
		}else
			return "open";	//otherwise is still open
	}

	public String bid(String bidder, double bid){
		if(bidder.equals(sellersName))
			return "A seller can't bid for is own items!";
		long millis=System.currentTimeMillis();
		Date currentdate=new java.util.Date(millis);
		if(contains(bidder)) {
			return raiseBid(bidder, bid, currentdate);
		}else listOfBid.add(new Bid(bidder, bid, currentdate));
		return checkAuction();
	}

	public String raiseBid(String bidder, double bid, Date currentdate) {
		int i = 0;
		while(i<listOfBid.size()) {
			if(listOfBid.get(i).getBidder().equals(bidder)) {
				if(bid>listOfBid.get(i).getBid()) {
					listOfBid.get(i).setBid(bid);
					listOfBid.get(i).setTime(currentdate);
					return "Your bid was raised to "+bid+"!";
				}else return "You can't lower your bid!";
			}
			i++;
		}
		return "error!";
	}

	private boolean contains(String bidder) {
		int i = 0;
		while(i<listOfBid.size()) {
			if(listOfBid.get(i).getBidder().equals(bidder))
				return true;
			i++;
		}
		return false;
	}

	public String toString() {
		return "Auction: "+name+" Description: "+description+" Status: "+checkAuction()+" Bids: "+listOfBid.size()+" Seller: "+sellersName;
	}

	public Bid getBestBid() {
		if(listOfBid.size()==0)
			return null;
		else if(listOfBid.size()==1)
			return listOfBid.get(0);
		else {
			Bid bestBid = listOfBid.get(0);
			int i = 1;
			while(i<listOfBid.size()) {
				if(bestBid.getBid()<listOfBid.get(i).getBid())
					bestBid = listOfBid.get(i);
				i++;
			}
			return bestBid;
		}
	}

	public String declareWinner() {
		Bid bid = getBestBid();
		if(bid==null) {
			return "Auction closed. No bids for this Auction!";
		}else {
			listOfBid.remove(bid);
			Bid bid2 = getBestBid();
			listOfBid.add(bid);
			if(bid2==null) {
				if(bid.getBid()<reservedPrice)
					return bid.getBidder()+ " was the only bidder. His bid was lower than Reserve Price. Auction was closed with no winner";
				else 
					return "Auction was closed. Best Bid: "+new Bid(bid.getBidder(), reservedPrice, bid.getTime()).toString();
			}else
				return "Auction was closed. Best Bid: "+new Bid(bid.getBidder(), max(bid2.getBid(), reservedPrice), bid.getTime()).toString();
		}
	}

	public double max(double bid1, double bid2) {
		if(bid1>bid2)
			return bid1;
		else return bid2;
	}
}