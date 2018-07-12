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
 * Reads data from N560 via ASIX Electronix Co Wireless adapter AX220xx RS232 to
 * WIFI after network disconnection the socket is reconnect.
 * 
 * @author xxx
 *
 */
public class AsixReader extends Thread implements N560DataServerInterface {

	private String ASIX_PROXY_OXYIP = "192.168.0.5"; // TODO make configurable
	private int ASIX_PROXY_OXYPORT = 5000; // TODO make configurable

	private Socket inputsocket = null;
	private Date prevDate = null;
	private N560Data n560Data = null;
	private InputStream instream;
	private DataInputStream datainputstream;
	private BufferedReader bufferedReader;


	/* server and port configuration */
	public AsixReader(String aSIX_PROXY_OXYIP, int aSIX_PROXY_OXYPORT) {
		ASIX_PROXY_OXYIP = aSIX_PROXY_OXYIP;
		ASIX_PROXY_OXYPORT = aSIX_PROXY_OXYPORT;
	}

	public AsixReader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * the thread loop
	 */
	public void run() {
		System.out.println("N560GetterandParserThread started");
		while (true) {
			try {
				System.out.println("Try to connect to ..."+ASIX_PROXY_OXYIP+"/"+ ASIX_PROXY_OXYPORT);
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
					if(instream!= null)
						instream.close();
					if(datainputstream != null)
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
		inputsocket.setSoTimeout(4000); // 4sec timeout then read returns no
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
	 * 
	 *  
 rad 8
02/24/18 04:48:53 SN=0000145838 SPO2=097% BPM=080 PI=04.96% SPCO=--.-% SPMET=--.-% DESAT=-- PIDELTA=+-- ALARM=0000 EXC=000800
02/24/18 04:48:54 SN=0000145838 SPO2=097% BPM=081 PI=05.04% SPCO=--.-% SPMET=--.-% DESAT=-- PIDELTA=+-- ALARM=0000 EXC=000800
02/24/18 04:48:55 SN=0000145838 SPO2=097% BPM=081 PI=05.04% SPCO=--.-% SPMET=--.-% DESAT=-- PIDELTA=+-- ALARM=0000 EXC=000800
02/24/18 04:48:56 SN=0000145838 SPO2=097% BPM=081 PI=05.13% SPCO=--.-% SPMET=--.-% DESAT=-- PIDELTA=+-- ALARM=0000 EXC=000800
02/24/18 04:48:57 SN=0000145838 SPO2=097% BPM=080 PI=05.26% SPCO=--.-% SPMET=--.-% DESAT=-- PIDELTA=+-- ALARM=0000 EXC=000800
02/24/18 04:48:58 SN=0000145838 SPO2=097% BPM=080 PI=05.31% SPCO=--.-% SPMET=--.-% DESAT=-- PIDELTA=+-- ALARM=0000 EXC=000800
	 */
	
	private boolean isMAssimo(String[] cols) {
		for ( String col:cols){
			if(col.startsWith("SPMET")){
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		AsixReader   a = new AsixReader();
		a.readAllDataFromSocket();
	}
	public String readLine() throws IOException {
		String lin = bufferedReader.readLine();
		return lin;
	}
	/*
	 * ->line from ASIX: 07-Jan-04 11:24:50   100     105      88                   AO
-->line from ASIX: 07-Jan-04 11:24:52   100     104      23                   AO
-->line from ASIX: 07-Jan-04 11:24:54   100     102      36    MO             AO
-->line from ASIX: 07-Jan-04 11:24:56   100     101     104    MO             AO
-->line from ASIX: 07-Jan-04 11:24:58   100     101      26                   AO
-->line from ASIX: N-560    VERSION 1.61.00    CRC:XXXX  SpO2 Limit: 95-100%    PR Limit: 59-148BPM
-->line from ASIX:                              ADULT           25SAT-S 
-->line from ASIX: TIME                 %SpO2   BPM     PA     Status
	 */
	public String readLinetestMAssimo() throws IOException {
	 
		return "02/24/18 04:48:58 SN=0000145838 SPO2=097% BPM=080 PI=05.31% SPCO=--.-% SPMET=--.-% DESAT=-- PIDELTA=+-- ALARM=0000 EXC=000800";
	}
	
	private N560Data readAllDataFromSocket() throws IOException {

		N560Data n560Data = null;
		String lin =  readLine();
		System.out.println("-->line from ASIX: " + lin);
		try {
			String[] cols = lin.split("[ ]+");
			if (cols.length <= 0) {
				return readAllDataFromSocket();// recursive call
			}
			if( isMAssimo(cols)){
				n560Data = new N560Data();
				System.out.println("MASSIMO " + cols[0] + " " + cols[1]);
				// 1. date
				DateFormat formatter1;
				formatter1 = new SimpleDateFormat("MM/DD/yy HH:mm:ss",Locale.US);
				
				try {
					Date datum = formatter1.parse(cols[0] + " " + cols[1]);
				
					System.out.println("datum " + datum);
					n560Data.setDatetime(datum);
					if (!checkDateDifferenceOk(datum)) {
						System.out.println("data missing after " + datum);
					}
				} catch (ParseException e1) {
					System.out.println("readAllDataFromSocket:Date: error ");
				}
				
				System.out.println("parsed data : sp02: " + cols[3]);			
				//SPO2=097%
				String sp02Str = cols[3];
				Integer sp02 = null;
				try {
				 
						sp02Str =sp02Str.replace("SPO2=", "");
						sp02Str =sp02Str.replace("%", "");
						
						if (sp02Str.equals("---")) {
							sp02 = null;
						} 
						else {
						sp02 = new Integer(sp02Str);  //leading 0 ???
						}
					 
				} catch (NumberFormatException e) {
				}
				n560Data.setSp02(sp02);
				
				// 3. Bpm  BPM=080
				System.out.println("parsed data : Bpm: " + cols[4]);
				String bpmStr = cols[4];
				Integer bpm = null;
				try {
					if (bpmStr.equals("---")) {
						bpm = null;
					} else {
						bpm = new Integer(bpmStr.replace("BPM=", ""));//leading 0 ???
					}
				} catch (NumberFormatException e) {
				}
				n560Data.setBpm(bpm);
				
				
				List<String> statusList = new ArrayList<String>();
				n560Data.setStatusList(statusList);
				n560Data.print();
			}
			else {   ///NELLLLLLLLLLLLLCORE
				if (cols[0].equals("N-560")) {
					// first HEADER LINE
					// N-560 VERSION 1.61.00 CRC:XXXX SpO2 Limit: 92-100% PR Limit:
					// 90-180BPM
				} else if (lin.startsWith(" ")) {
					// second HEADER LINE
					// ADULT 0SAT-S
				} else if (cols[0].equals("TIME")) {
					// Value Header
					// TIME %SpO2 BPM PA Status
				} else {// DATA
					n560Data = new N560Data();
					// 02-Feb-00 13:26:32 100 139 30 MO
					String datetimestr = cols[0] + " " + cols[1];
					System.out.println("parsed data :datetimestr: "+datetimestr);
					 
					// 1. date
					DateFormat formatter1;
					formatter1 = new SimpleDateFormat("dd-MMM-yy HH:mm:ss",Locale.US);
					try {
						Date datum = formatter1.parse(cols[0] + " " + cols[1]);
						System.out.println("datum " + datum);
						n560Data.setDatetime(datum);
						if (!checkDateDifferenceOk(datum)) {
							System.out.println("data missing after " + datum);
						}
					} catch (ParseException e1) {
						System.out.println("readAllDataFromSocket:Date: error ");
					}
					// 2. sp02
					System.out.println("parsed data : sp02: " + cols[2]);
					String sp02Str = cols[2];
					Integer sp02 = null;
					try {
						if (sp02Str.equals("---")) {
							sp02 = null;
						} else {
							sp02 = new Integer(sp02Str.replace("*", ""));
						}
					} catch (NumberFormatException e) {
					}
					n560Data.setSp02(sp02);

					// 3. Bpm
					System.out.println("parsed data : Bpm: " + cols[3]);
					String bpmStr = cols[3];
					Integer bpm = null;
					try {
						if (bpmStr.equals("---")) {
							bpm = null;
						} else {
							bpm = new Integer(bpmStr.replace("*", ""));
						}
					} catch (NumberFormatException e) {
					}
					n560Data.setBpm(bpm);

					// 4. PA ;
					String paStr =cols[4];
					System.out.println("parsed data :Pa: " + paStr);
					Integer pa = null;
					try {
						if (paStr == null || paStr.equals("---")) {
							pa = null;
						} else {
							pa = new Integer(paStr.replace("*", ""));
						}
					} catch (NumberFormatException e) {
					}
					n560Data.setPa(pa);
					// 5. Status
					List<String> statusList = new ArrayList<String>();
					if (cols.length == 6) {
						System.out.println("parsed data : Status:" + cols[5]);
						// TODO MORE STATI POSSIBLE .......parse them
						statusList.add(cols[5]);
					}
					n560Data.setStatusList(statusList);
					n560Data.print();
				}
					
			}
		} catch (Exception e) {// in case the data format is scrublemd
			e.printStackTrace();
			System.out.println("---Exception receive error"+e.getMessage());
			System.out.println("---read next line ");
			return readAllDataFromSocket();
		}
		if (n560Data != null) {
			return n560Data;
		} else { // Header was read --> read next line
			return readAllDataFromSocket();
		}

	}

	

	/**
	 * assert a data record all 2 Seconds
	 * 
	 * @param datum
	 * @return
	 */
	private boolean checkDateDifferenceOk(Date datum) {
		if (prevDate == null) {
			prevDate = datum;
			return true;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(prevDate);
		cal.add(Calendar.SECOND, 2);
		if (!datum.equals(cal.getTime())) {
			prevDate = datum;
			return false;
		} else {
			prevDate = datum;
			return true;
		}
	}
}
