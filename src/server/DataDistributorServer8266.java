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
public class DataDistributorServer8266 extends Thread {

	private static int SERVERPORT = 5001; // TP make configurable
	private static N560DataServerInterface readN560DataThread = null;
	private Socket connectionSocket;
	private int samedateCount;

	public DataDistributorServer8266(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public DataDistributorServer8266() {
	}

	/**
	 * main thread for acception
	 * 
	 */
	public static void main(String[] args) {
		boolean ok = false;
		readN560DataThread = new ESP8266Reader();
		readN560DataThread.start();
		try {
			Thread.sleep(2000); // wait for initial data avail
		} catch (InterruptedException e) {
		}
		while (!ok) {
			try {
				new DataDistributorServer8266().listenToPort();
				ok = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void listenToPort() throws Exception{
		ServerSocket welcomeSocket = null;
		try {
			welcomeSocket = new ServerSocket(SERVERPORT);
		} catch (Exception e) {
			e.printStackTrace();
			if(welcomeSocket != null){
				welcomeSocket.close();
			}
			throw e;
		}
		while (true) { // wait for clients
			Socket connectionSocket = null;
			try {
				connectionSocket = welcomeSocket.accept();
			} catch (IOException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
				throw ee;
			}
			// create a new thread for user
			Thread service = new DataDistributorServer8266(connectionSocket);
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