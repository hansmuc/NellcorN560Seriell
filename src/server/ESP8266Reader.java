package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.N560Data;
import interfaces.N560DataServerInterface;
import mswindowsClient.WindowsDisplay;
import rules.NoDataException;

/**
 * Reads data from BLuetooth Pulsoxy via ESP8266
 * 
 * @author xx
 *
 */
public class ESP8266Reader extends Thread implements N560DataServerInterface {

	private String ASIX_PROXY_OXYIP = "192.168.0.22"; // TODO make configurable
	private int ASIX_PROXY_OXYPORT = 21; // TODO make configurable

	private Socket inputsocket = null;
	private Date prevDate = null;
	private N560Data n560Data = null;
	private InputStream instream;
	private DataInputStream datainputstream;
	private BufferedReader bufferedReader;
	private Integer lastrunningnoNO = 0;

	/* server and port configuration */
	public ESP8266Reader(String aSIX_PROXY_OXYIP, int aSIX_PROXY_OXYPORT) {
		ASIX_PROXY_OXYIP = aSIX_PROXY_OXYIP;
		ASIX_PROXY_OXYPORT = aSIX_PROXY_OXYPORT;
	}

	public ESP8266Reader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * the thread loop
	 */
	public void run() {
		System.out.println("ESP8266Reader started");
		while (true) {
			try {
				System.out.println("Try to connect to ..." + ASIX_PROXY_OXYIP + "/" + ASIX_PROXY_OXYPORT);
				connect();
				System.out.println("Connection established ...readloop ...");
				readLoop();
			} catch (IOException e) {
				System.out.println(e.fillInStackTrace());

				// close all objects && wait 1 sec befo
				try {
					if (inputsocket != null)
						inputsocket.close(); // java.net.SocketTimeoutException:
												// Read timed out
					if (instream != null)
						instream.close();
					if (datainputstream != null)
						datainputstream.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		} // while
	}

	/**
	 * connect to ASIX
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void connect() throws UnknownHostException, IOException {
		inputsocket = new Socket(ASIX_PROXY_OXYIP, ASIX_PROXY_OXYPORT);
		inputsocket.setSoTimeout(10000); // 4sec timeout then read returns no
										// block!!!
		instream = inputsocket.getInputStream();
		datainputstream = new DataInputStream(instream);
		bufferedReader = new BufferedReader(new InputStreamReader(datainputstream));
	}

	/**
	 * Data access form outside
	 */
	public synchronized N560Data getN560Data() throws NoDataException {
		if (n560Data != null) {
			return n560Data;
		} else {
			throw new NoDataException();
		}
	}

	public void readLoop() throws IOException {
		while (true) {
			N560Data currentn560Data = readAllDataFromSocket();
			if (currentn560Data != null) {
				n560Data = currentn560Data;
			}
		}
	}

	/**
	 * Read and parse from ASIX's
	 * 
	 * @return
	 * @throws IOException
	 */
	private N560Data readAllDataFromSocket() throws IOException {

		N560Data n560Data = null;
		String[] cols = null;
		while (cols == null || cols.length != 3) {
			String lin = bufferedReader.readLine();
			System.out.println("-->line from 8266: " + lin);
			cols = lin.split(";");
		}
		{// DATA
			n560Data = new N560Data();
			// 02-Feb-00 13:26:32 100 139 30 MO
			String runningno = cols[0];

			System.out.println("parsed data :runningno: " + runningno);
			String puls = cols[1];
			System.out.println("parsed data :puls: " + puls);
			String spo2 = cols[2];
			System.out.println("parsed data :spo2: " + spo2);

			Integer runningnoNO = null;
			Integer pulsNO = null;
			Integer spo2NO = null;

			try {
				runningnoNO = new Integer(runningno);
				pulsNO = new Integer(puls);
				spo2NO = new Integer(spo2);
				Date now = new Date();
				if (runningnoNO > lastrunningnoNO) {
					n560Data.setDatetime(now);
					System.out.println("set time:" + now.toLocaleString());
					System.out.println();
				}

			} catch (NumberFormatException e) {
				// incorect was read --> read next line
				return readAllDataFromSocket();
			}

			n560Data.setSp02(spo2NO);
			n560Data.setBpm(pulsNO);
			lastrunningnoNO = runningnoNO;
		}
		return n560Data;

	}

}
