package clientReceiver;



import java.io.BufferedReader;     




import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import interfaces.UpdateableDisplay;
import rules.AlertColorAndSound;
import rules.Decider;
import rules.DecisionConstants;

public class DisplayReceiver extends Thread {
	
	private final static Logger LOGGER = Logger.getLogger(DisplayReceiver.class.getName());
	private BufferedReader inFromServer;
	private Date prevDatadate = null;
	private int samedateCount = 0;
	private Date lastDate;
	private final int MAX_MILLI_FORSAMEDATA = 5000;
	private DecisionConstants decisionParams;
	private UpdateableDisplay updateableDisplay;

	private Decider decider;
	
	public DisplayReceiver(UpdateableDisplay updateableDisplay, DecisionConstants decisionParams){
		this.decider = new Decider(decisionParams);
		this.updateableDisplay = updateableDisplay;
		this.decisionParams = decisionParams;
	}


	private Socket clientSocket;

	private void openConnection() throws UnknownHostException, IOException {
		LOGGER.log(Level.INFO, "try connect to PULSOXYSERVER " + decisionParams.PULSOXYSERVER_ID);
		clientSocket = new Socket(decisionParams.PULSOXYSERVER_ID, decisionParams.PULSOXYSERVER_PORT);
		clientSocket.setSoTimeout(3000);// raises SocketTimeoutException
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		 System.out.println("openConnection");
	}

	public void run() {
		

		while (true) { // try connect/reconnect
			try {
				openConnection();
				String modifiedSentence = "";
				String oldmodifiedSentence = "";
				while (true) {// read foreever
					
						Date beforeloop = new Date();
						// busy loop
						while (modifiedSentence.equals(oldmodifiedSentence)) {
							  System.out.println("loop");
							try {
								modifiedSentence = inFromServer.readLine();
							} catch ( IOException e) { //includes SocketTimeoutException on timeout hanging
								modifiedSentence = oldmodifiedSentence;
								break;
							}
					 
							Date now = new Date();
							if (now.getTime() - beforeloop.getTime() > 1000 * 3) {
								break;// more than 3 seconds same info --->
										// hanging server
							}
						} // loopwave.destroy();
		 
						oldmodifiedSentence = modifiedSentence;
						// System.out.println("modifiedSentence=" +
						// modifiedS//read foreeverentence);
						String split[] = modifiedSentence.split(";");

						// 1. field datetime
						String datetime = split[0];
						// Mon Apr 11 12:11:00 CEST 2016;100;139;214;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US);

						Date dataDate = null;
						try {
							dataDate = sdf.parse(datetime);
							lastDate = dataDate;
						} catch (ParseException e1) {
						}

						boolean ddok = dateDifferenceIsok(dataDate, prevDatadate);
						prevDatadate = dataDate;
						// 2. o2 datetime null = "---" or integer
						String sp02 = split[1];
						String puls = split[2];	
						if (sp02.equals("null")) {
							// System.out.println("sp02 is null")
							updateClient(ReceiveStatus.nc, null, ddok, dataDate,puls);
						} else {
							try {			
								updateClient(ReceiveStatus.ok, new Integer(sp02), ddok, dataDate,puls);
							} catch (Exception e) {
							}
						}
											 
						LOGGER.log(Level.INFO, "From Server: Date/02/ " + datetime + "/" + sp02);
					
				} // read foreever
			} // try while
			catch (UnknownHostException e) {
				LOGGER.log(Level.INFO, "UnknownHostException " + e.getMessage());
			// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			catch (Exception e) {// ?? IOException
					updateClient(ReceiveStatus.serverlost, null, false, null,null);
					e.printStackTrace();
			}
		}

	}// run


	/**
	 * PULSOXYSERVER_ID the datarecords current and previous hava a small
	 * enought time gap
	 * 
	 * @param currentDataDate
	 * @param prevDatadate
	 * @return
	 */
	private boolean dateDifferenceIsok(Date currentDataDate, Date prevDatadate) {
		if (prevDatadate == null) {
			return true;
		}
		long diff = currentDataDate.getTime() - prevDatadate.getTime();
		long diffmilliSeconds = diff;

		if (diffmilliSeconds == 0) {
			samedateCount++;
		} else {
			samedateCount = 0;
		}
		if (diffmilliSeconds == 00 || diffmilliSeconds > MAX_MILLI_FORSAMEDATA || samedateCount > 4) {
			return false;
		} else {
			return true;
		}
	}

	private void updateClient(ReceiveStatus status, Integer sp02, boolean ddok, Date dataDate, String puls) {
		boolean newerDataArrived = false;
		boolean serverLost = true;
		Date n560Date = null;
		Integer iSpo2 = null;
		if (status.equals(ReceiveStatus.ok)) {
			iSpo2 = sp02;
			newerDataArrived = ddok;
			n560Date = dataDate;
			serverLost = false;
		}
		if (status.equals(ReceiveStatus.nc)) {
			iSpo2 = null;
			newerDataArrived = ddok;
			n560Date = dataDate;
			serverLost = false;
		}
		if (status.equals(ReceiveStatus.serverlost)) {
			iSpo2 = null;
			n560Date = dataDate;
			newerDataArrived = false;
			serverLost = true;
		}
		AlertColorAndSound cs = decider.decideAlert(iSpo2, n560Date, newerDataArrived, serverLost,puls);
		updateableDisplay.update(cs );
	}

}
