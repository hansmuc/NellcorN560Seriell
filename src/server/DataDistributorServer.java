package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import interfaces.N560DataServerInterface;
import rules.NoDataException;

/**
 * At this server Display could connect
 * 
 * @author xx
 * 
 *
 */
public class DataDistributorServer extends Thread {

	private static int SERVERPORT = 5001; // TP make configurable
	private static N560DataServerInterface readN560DataThread = null;
	private Socket connectionSocket;
	private int samedateCount;

	public DataDistributorServer(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public DataDistributorServer() {
	}

	/**
	 * main thread for acception
	 * 
	 */
	public static void main(String[] args) {

		readN560DataThread = new AsixReader();
		readN560DataThread.start();
		try {
			Thread.sleep(2000); // wait for initial data avail
		} catch (InterruptedException e) {
		}
		new DataDistributorServer().listenToPort();
	}

	public void listenToPort() {
		ServerSocket welcomeSocket = null;
		try {
			welcomeSocket = new ServerSocket(SERVERPORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (true) { // wait for clients
			Socket connectionSocket = null;
			try {
				connectionSocket = welcomeSocket.accept();
			} catch (IOException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}
			// create a new thread for user
			Thread service = new DataDistributorServer(connectionSocket);
			service.start();
		}

	}

	/*
	 * Write info to socket for read of Monitor etc Delivers to socket the LAST
	 * ( may be older) record form pulsoxy
	 * 
	 * @see java.lang.Thread#run()p on
	 */
	public void run() {
		while (true) { // write every minute records to client
			try {
				
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				String marschalled = readN560DataThread.getN560Data().marschall();

				System.out.println("--> " + marschalled);
				outToClient.writeBytes(marschalled);
				 try {
					 Thread.sleep(2000);// 1 second
					 } catch (InterruptedException e) {
					 }
			} catch (IOException | NoDataException e) {
				System.out.println("IOException " + e.getMessage());// pipe
																	// broken
				break;// --i.e.exit thread
			}

		}
	}
}