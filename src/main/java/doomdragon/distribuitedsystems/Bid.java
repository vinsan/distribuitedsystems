package doomdragon.distribuitedsystems;

import java.io.Serializable;
import java.util.Date;

public class Bid implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3352156139766182625L;
	private String bidder;
	private double bid;
	private Date time;

	public Bid(String bidder, double bid, Date currentdate) {
		this.setBidder(bidder);
		this.setBid(bid);
		this.setTime(currentdate);
	}

	public String getBidder() {
		return bidder;
	}

	public void setBidder(String bidder) {
		this.bidder = bidder;
	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	public String toString() {
		return bidder +" bid "+bid+" on "+time.toString();
	}
}