package tools;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import interfaces.N560DataServerInterface;
import rules.NoDataException;

/**
 * At this server Display could connect
 * 
 * @author xx
 * 
 *
 */
public class DataDistributorTest extends Thread {

	private static int SERVERPORT = 5001; // TP make configurable
	 
	private Socket connectionSocket;
	private int samedateCount;

	public DataDistributorTest(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public DataDistributorTest() {
	}

	/**
	 * main thread for acception
	 * 
	 */
	public static void main(String[] args) {

		new DataDistributorTest().listenToPort();
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
			Thread service = new DataDistributorTest(connectionSocket);
			service.start();
		}

	}

	
	String marschall(Date datetime ,String sp02,String bpm){
		String res ="";
		SimpleDateFormat sdf = new  SimpleDateFormat("yyyy.MM.dd HH:mm:ss",Locale.US);
		sdf.format(datetime);
		res += sdf.format(datetime) + ";";
		res += sp02 + ";";
		res += bpm + ";";
		res += "255" + ";";
		res += "" + "\n";
		System.out.println("--> " + res);
		return res;
	}
	/*
	 * Write info to socket for read of Monitor etc Delivers to socket the LAST
	 * ( may be older) record form pulsoxy
	 * 
	 * @see java.lang.Thread#run()p on
	 * 
	 * 2016.10.02 18:00:06;99;143;255;MO,
	 */
	public void run() {
		 
			try {
				
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			//	String marschalled = readN560DataThread.getN560Data().marschall();

			//	System.out.println("--> " + marschalled);
			//	outToClient.writeBytes(marschalled);
				
				for (int i = 1; i<=5; i++){
				 try {
					 Thread.sleep(2000);// 1 second
					 } catch (InterruptedException e) {
					 }
				 outToClient.writeBytes(marschall(new Date() ,"99","122")) ;
				}
				for (int i = 1; i<=20; i++){
					 try {
						 Thread.sleep(2000);// 1 second
						 } catch (InterruptedException e) {
						 }
					 outToClient.writeBytes(marschall(new Date() ,"98","122")) ;
					}
				for (int i = 1; i<=20; i++){
					 try {
						 Thread.sleep(2000);// 1 second
						 } catch (InterruptedException e) {
						 }
					 outToClient.writeBytes(marschall(new Date() ,"88","122")) ;
					}
				for (int i = 1; i<=10; i++){
					 try {
						 Thread.sleep(2000);// 1 second
						 } catch (InterruptedException e) {
						 }
					 outToClient.writeBytes(marschall(new Date() ,"88","122")) ;
					}
				for (int i = 1; i<=10; i++){
					 try {
						 Thread.sleep(2000);// 1 second
						 } catch (InterruptedException e) {
						 }
					 outToClient.writeBytes(marschall(new Date() ,"79","122")) ;
					}
				for (int i = 1; i<=10; i++){
					 try {
						 Thread.sleep(2000);// 1 second
						 } catch (InterruptedException e) {
						 }
					 outToClient.writeBytes(marschall(new Date() ,"99","122")) ;
					}
			} catch (IOException   e) {
				System.out.println("IOException " + e.getMessage());// pipe
																	// broken
				 
			}

		}
	 
}