package doomdragon.distribuitedsystems;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for Auction Mechanism with four peers.
 */
public class FourPeersTest 
extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public FourPeersTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( FourPeersTest.class );
	}

	/**
	 * Rigourous Test :-)
	 * @throws Exception 
	 */
	public void testApp() throws Exception
	{
		//launch 4 peers
		AuctionPeer peer0 = new AuctionPeer(0, "peer0", "127.0.0.0");
		AuctionPeer peer1 = new AuctionPeer(1, "peer1", "127.0.0.1");
		AuctionPeer peer2 = new AuctionPeer(2, "peer2", "127.0.0.1");
		AuctionPeer peer3 = new AuctionPeer(3, "peer3", "127.0.0.1");
		//test getUsername
		assertEquals("peer0", peer0.getUsername());
		long millis=System.currentTimeMillis()-1;
		Date date = new java.util.Date(millis);
		//test createAuction with negative reserve price
		assertFalse(peer0.createAuction("Cellulare", date, -1, "Samsung Galaxy S7"));
		//test createAuction with expired date
		assertFalse(peer0.createAuction("Cellulare", date, 100, "Samsung Galaxy S7"));
		millis=System.currentTimeMillis()+20000;	//this auction will expire in 20 seconds
		date = new java.util.Date(millis);
		//test createAuction
		assertTrue(peer0.createAuction("Cellulare", date, 100, "Samsung Galaxy S7"));
		//test cancelAuction of another user
		assertEquals("You cannot cancel another user's auction", peer2.cancelAuction("Cellulare"));
		//test cancelAuction with no offers
		assertEquals("Auction successfully removed", peer0.cancelAuction("Cellulare"));
		//test cancelAuction with a non existing auction
		assertEquals("auction Computer not found!", peer3.cancelAuction("Computer"));
		//test checkAuction for a deleted auction
		assertEquals("not found", peer0.checkAuction("Cellulare"));
		//print No auctions!
		peer0.printAllAuction();
		peer0.createAuction("Cellulare", date, 100, "Samsung Galaxy S7");
		//test setReservePrice to a negative number
		assertEquals("You can't have a negative reserve price!", peer0.setReservePrice("Cellulare", -1));
		//test setReservePrice of another user's auction
		assertEquals("You cannot change another user's auction's reserve price!", peer1.setReservePrice("Cellulare", 1));
		//test setReservePrice with a non existing auction
		assertEquals("auction Computer not found!", peer2.setReservePrice("Computer", 100));
		//test setReservePrice with correct params
		assertEquals("Reserve price for auction Cellulare set to 200.0", peer0.setReservePrice("Cellulare", 200.00));
		//test checkAuction for an open auction
		assertEquals("open", peer1.checkAuction("Cellulare"));
		millis=System.currentTimeMillis()-1;
		Date expireDate=new java.util.Date(millis);
		//test changeEndTime with an expired date
		assertEquals("New End Time is before current date!", peer0.changeEndTime("Cellulare", expireDate));
		millis=System.currentTimeMillis()+5000;	//this auction will expire in 5 seconds
		Date newDate=new java.util.Date(millis);
		//test changeEndTime of another user
		assertEquals("You cannot change the end date of another user's auction!", peer1.changeEndTime("Cellulare", newDate));
		//test changeEndTime with correct params
		assertEquals("The auction for Cellulare will be closed on "+newDate, peer0.changeEndTime("Cellulare", newDate));
		//test changeEndTime with a non existing auction
		assertEquals("auction Computer not found!", peer3.changeEndTime("Computer", newDate));
		//test placeABid
		assertEquals("open", peer1.placeAbid("Cellulare", 100.00));
		assertEquals("open", peer2.placeAbid("Cellulare", 150.00));
		assertEquals("open", peer3.placeAbid("Cellulare", 200.00));
		//test lower a bid
		assertEquals("You can't lower your bid!", peer1.placeAbid("Cellulare", 90.00));
		//test raise a bid
		assertEquals("Your bid was raised to 210.0!", peer3.placeAbid("Cellulare", 210));
		//test placeABid from a seller
		assertEquals("A seller can't bid for is own items!", peer0.placeAbid("Cellulare", 250.00));
		//test cancelAuction on an auction with bids
		assertEquals("Someone made bids for this auction. You can't delete it", peer0.cancelAuction("Cellulare"));
		//test changeEndTime on an auction with bids
		assertEquals("You cannot change the end date for an auction with offers!", peer0.changeEndTime("Cellulare", newDate));
		millis=System.currentTimeMillis()+2000;
		expireDate=new java.util.Date(millis);
		peer1.createAuction("Computer", expireDate, 100, "Computer Gaming MSI");
		while(System.currentTimeMillis()<millis) {
			//loop to let the auction expire
		}
		//check a not open auction
		assertNotSame("open", peer2.checkAuction("Computer"));
		//test setReservePrice for a closed auction
		assertEquals("Computer is already closed!", peer2.setReservePrice("Computer", 300.0));
		millis = newDate.getTime();
		while(System.currentTimeMillis()<millis) {
			//loop to let another auction expire
		}
		//check another not open auction
		assertNotSame("open", peer1.checkAuction("Cellulare"));
		long reOpenTime = System.currentTimeMillis()+2000;
		//try to re-open an auction, but fails
		assertEquals("You can't re-open an auction for a sold item!", peer0.changeEndTime("Cellulare", new java.util.Date(reOpenTime)));
		//try to re-open an auction, success
		assertEquals("The auction for Computer has been reopened", peer1.changeEndTime("Computer", new java.util.Date(reOpenTime)));
		peer2.placeAbid("Computer", 90);
		while(System.currentTimeMillis()<reOpenTime) {
			//loop to let computer auction expire
		}
		//test declareWinner with a bid lower than reserve price
		assertEquals("peer2 was the only bidder. His bid was lower than Reserve Price. Auction was closed with no winner", peer3.checkAuction("Computer"));
		reOpenTime = System.currentTimeMillis()+2000;
		peer1.createAuction("Tastiera", new java.util.Date(reOpenTime), 20, "Tastiera gaming completa di mouse");
		peer2.placeAbid("Tastiera", 110);
		while(System.currentTimeMillis()<reOpenTime) {
			//loop to let computer auction expire
		}
		assertTrue((peer0.checkAuction("Tastiera").contains("Auction was closed. Best Bid:")));
		//printAllAuction
		peer0.printAllAuction();
		//shutdown all peers
		peer0.shutdown();
		peer1.shutdown();
		peer2.shutdown();
		peer3.shutdown();

	}
}