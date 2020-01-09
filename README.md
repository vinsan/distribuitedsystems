# Auction mechanism

An auction mechanism based on P2P Network in wich each peer can sell and buy goods using a Second-Price Auction mechanism (like E-Bay)

## Implementation

### Basic operations

- createAuction: creates a new auction for a good;
- checkAuction: Checks the status of the auction;
- placeAbid: Places a bid for an auction if it is not already ended.

### Additional operation

- printAllAuction: prints all the auction stored on the master peer. This operation will print both the open auctions and the closed auctions
- cancelAuction: allows the creator of an auction with no bids to delete that auction. This operation will fail if there is one or more bid on the auction or if it's called by another peer other than the seller's
- setReservePrice: this operation allows the creator of an auction to raise (or lower) the reserve price. This operation will fail if the reserve price is set to a value lower than 0 (a negative double) or if it's called by another peer other than the seller's
- changeEndTime: allows the creator of an auction with no bids to change the end time of that auction. This operation will fail if it's called with an already expired end date, if it's called by another peer other than the seller's, or if it's called on an already closed auction. This function can also be used to re-open an auction that was closed with no winner (only if called by the seller).

### Technologies

The project has been implemented with:

- Java 8
- Apache Maven
- Eclipse IDE
- Tom p2p
- JUnit
- Docker

## Project Structure

The main program is structured in five Java classes: 

- Auction: a class thet contains all the informations about an auction.
- AuctionMechanism the API that define the 3 basic operations of the project.
- AuctionPeer the implementation of the API.
- Bid: a class thet contains all the informations about a bid.
- Main: a command line interface to interact with the system.

The project provide also the classes AuctionTest, FourPeersTest and MainTest which are JUnit test cases. FourPeersTest instantiates a network of 4 peers to test all the operations in the project, while MainTest is for testing the non-interactive methods of the Main (with a coverage of 54.1% due the interactive nature of the command line interface) and AuctionTest is for testing some basic operations on an Auction wich are not covered in the network test (Bid.toString(), Bid.getTime(), max() and the raise function when it's called with a fake param). The coverage for Auction and Bid is 100% while the coverage for AuctionPeer is 95.4% (the parts not covered in the test case are due to instructions wich are used in case of IOException or ClassNotFoundException). 

## How to build app in a Docker container

Use the command:
docker build --no-cache -t distribuitedsystems .

Then start the master peer: 
docker run -i --name ENTERNAMEHERE distribuitedsystems

This will start the Master-Peer in the container ENTERNAMEHERE. By default, the peer will start on port 4000,with IP Address 127.0.0.0 and the seller's name will be "default".

How to check your Master Peer docker : docker ps

How Check the IP address of your Master IP Docker: docker inspect containerID

You can also use 
docker run -i --name ENTERNAMEHERE -e NAME="enternamehere" -e ID=1 -e HOSTNAME="172.17.0.2" distribuitedsystems

This will start the peer in the container ENTERNAMEHERE with ID 1 (or the number you enter after ID) with IP Address 172.17.0.2 (or the number you enter after HOSTNAME) and the seller's name will be enternamehere or what you enter after NAME.

### Developed by:
Vincenzo Santoro
0522500487
