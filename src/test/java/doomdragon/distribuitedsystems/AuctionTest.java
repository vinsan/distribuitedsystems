package doomdragon.distribuitedsystems;

import java.util.Date;
import java.util.GregorianCalendar;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for Auction.
 */
public class AuctionTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AuctionTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AuctionTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	Date date = (new GregorianCalendar(2020, 9, 22, 20, 0)).getTime();
		Auction test = new Auction("Cellulare", date, 200, "Samsung Galaxy S7", "doomdragon");
		Bid bid = new Bid("doomdragon", 100, date);
		//test Bid.getTime
		assertEquals(date, bid.getTime());
		//test Bid.toString
		assertEquals("doomdragon bid 100.0 on "+date.toString(), bid.toString());
		//test max
		assertEquals(1.0, test.max(0, 1));
		assertEquals(1.0, test.max(1, 0));
		//test raise
		assertEquals("error!", test.raiseBid("error", 1.0, date));
    }
}