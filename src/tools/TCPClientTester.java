package tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClientTester {
	public static void main(String argv[]) throws Exception {

		String modifiedSentence;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
				System.in));
		Socket clientSocket = new Socket("localhost", 5001);

		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		while (true) {
			modifiedSentence = inFromServer.readLine();
			String split[] =modifiedSentence.split(";");
			System.out.println("FROM SERVER: " + modifiedSentence +" "+split[1]);
		}
	//	clientSocket.close();
	}
}