package doomdragon.distribuitedsystems;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for Main.
 */
public class MainTest 
extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public MainTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( MainTest.class );
	}

	/**
	 * Rigourous Test :-)
	 * @throws Exception 
	 */
	public void testApp() throws Exception
	{
		AuctionPeer peer0 = Main.login();
		HashMap<String, String> env = new HashMap<String, String>();
		env.put("ID", "1");
		env.put("NAME", "user");
		env.put("HOSTNAME", "127.0.0.1");
		setEnv(env);
		AuctionPeer peer1 = Main.login();
		Main.printMenu();
		Scanner input = new Scanner(System.in);
		assertEquals("There was an error in the Auction creation!", Main.create("Cellulare", peer0, 0.0, "Nuovo", 2018, 11, 31, 0, 0));
		assertEquals("The Auction has been created!", Main.create("Cellulare", peer0, 0.0, "Nuovo", 2020, 11, 31, 23, 59));
		assertEquals("Status for the auction for Cellulare is: open", Main.check(peer0, "Cellulare"));
		assertTrue(Main.bid(peer0, "Cellulare", 100.0).contains("You didn't bid for this auction."));
		assertEquals("Auction is open. Bid was successfull.", Main.bid(peer1, "Cellulare", 100.0));
		Main.changePrice(peer1, "Cellulare", 80.0);
		Main.changeDate(peer1, "Cellulare", 2021, 11, 31, 23, 59);
		Main.cancel(peer1, "Cellulare");
		assertTrue(Main.exec("all", input, peer0));
		assertTrue(Main.exec("test", input, peer0));
		assertFalse(Main.exec("close", input, peer0));
		Main.logout(peer0, new Scanner(System.in));
		assertFalse(Main.exec("exit", input, peer1));
		Main.logout(peer1, input);

	}

	@SuppressWarnings("unchecked")
	protected static void setEnv(Map<String, String> newenv) throws Exception {
		Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
		Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
		theEnvironmentField.setAccessible(true);
		Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
		env.putAll(newenv);
		Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
		theCaseInsensitiveEnvironmentField.setAccessible(true);
		Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
		cienv.putAll(newenv);
	}

}
