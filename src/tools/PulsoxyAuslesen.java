package tools;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import interfaces.N560DataServerInterface;
import rules.NoDataException;
import server.AsixReader;

public class PulsoxyAuslesen extends Thread {
	static int SERVERPORT = 5001;
	static N560DataServerInterface readN560DataThread = null;
	private Socket connectionSocket;
	private int samedateCount;

	public static void main(String argv[])  {
				 
		readN560DataThread = new AsixReader();		 
		Thread readOxyDataFromN560thread = (Thread)readN560DataThread;
		readOxyDataFromN560thread.start();
		try {//wait 1sec
			Thread.sleep(1000);
		} catch (Exception e) {
		}		
		PulsoxyAuslesen tCPServer = new PulsoxyAuslesen();
		tCPServer.listenToPort();
	}

	
	public PulsoxyAuslesen(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public PulsoxyAuslesen() {
	}

	public void listenToPort() {
		ServerSocket welcomeSocket = null;
		try {
			 welcomeSocket = new ServerSocket(SERVERPORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (true) {	// wait for clients		 
			Socket connectionSocket = null;	
			try {
				 connectionSocket = welcomeSocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread service = new PulsoxyAuslesen(connectionSocket);
			service.start();
		}
	}


	public void run() {
		while (true) { // write every minute records to client
			try {
		
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				String marschalled = readN560DataThread.getN560Data().marschall();
				readN560DataThread.getN560Data().print();
				System.out.println("marschalled="+marschalled);
				outToClient.writeBytes(marschalled);
			} catch (IOException | NoDataException e) {

			}

		}
	}
}