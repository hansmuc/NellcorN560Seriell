package rules;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Decider implements DeciderInterface{

	private final static Logger LOGGER = Logger.getLogger(Decider.class.getName());

	private static final int NO_DATA_QUIET_FOR_SECONDS = 20;

	private static final int SENSOR_CHANGE_QUIET_FOR_SECONDS = 20;
	private static final int MAX100_FOR_SECONDS = 60*4;

	private Date prevDatadate = null;
	private int samedateCount = 0;
	private Date lastDate;
	private final int MAX_MILLI_FORSAMEDATA = 5000;
	private DecisionConstants decisionParams;
	private Date lastGreentime;

	private AlertColor lastStatus=AlertColor.GREEN;

	private String lastSp02;
	
	public Decider(DecisionConstants decisionParams) {
		this.decisionParams = decisionParams;
	}

	public AlertColorAndSound decideAlert(Integer iSpo2, Date currentN560Date, boolean newerDataArrived, boolean serverLost, String puls) {
		if (serverLost || currentN560Date == null) {
			LOGGER.log(Level.SEVERE, "NO SERVER ");
			return new AlertColorAndSound(AlertColor.BLUE, true, "NO SERVER", "???","???");
		}
		// from here up N560Date != null
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
		String hhmmss = sdf.format(currentN560Date);

		if (!newerDataArrived) { // no new data
			boolean sound = true;
			AlertColor ac=AlertColor.BLUE;
			String sp02 = "";//iSpo2.toString();
			LOGGER.log(Level.SEVERE, "NO DATA date/ ");
			if(lastStatus==AlertColor.RED){
				 sound = true;   //RED --> immediate Sound
				 ac=AlertColor.RED;
				 sp02=lastSp02;
			}
			else if (lastGreentimeSecondsAgo(NO_DATA_QUIET_FOR_SECONDS, currentN560Date)) {
				sound = false;
				ac=AlertColor.BLUE;
				sp02="???";
			}
			return new AlertColorAndSound(ac, sound, "NO DATA", sp02,"???");
		} else {
			if (iSpo2 == null) { /* NO SENSOR */
				LOGGER.log(Level.SEVERE, "NO SENSOR " + hhmmss + " ");
				boolean sound = true;
				String sp02 = lastSp02;
				AlertColor ac=AlertColor.BLUE;
				if(lastStatus==AlertColor.RED){
					 sound = true;   //RED --> immediate Sound
					 ac=AlertColor.RED;
					 sp02=lastSp02;
				}
				else if (lastGreentimeSecondsAgo(SENSOR_CHANGE_QUIET_FOR_SECONDS,	currentN560Date)) { /* give time to change sensor */
					sound = false;
					ac=AlertColor.BLUE;
					sp02=lastSp02;
				}
				return new AlertColorAndSound(ac, sound, hhmmss, sp02,puls);
			}
			if (iSpo2.intValue() <= decisionParams.RED_HIGH_LIMIT) {
				int pulsint = -1;
			    try {
					new Integer(puls).intValue();
				} catch (NumberFormatException e) {
				}
				if(iSpo2.intValue()==0 && pulsint == 0){
					if (lastGreentimeSecondsAgo(SENSOR_CHANGE_QUIET_FOR_SECONDS,	currentN560Date)) {
						return new AlertColorAndSound(AlertColor.BLUE,false,hhmmss,"0","0");
					}
					else {
						return new AlertColorAndSound(AlertColor.BLUE,true,hhmmss,"0","0");
					}
				}
				else {
					LOGGER.log(Level.INFO, "SMALLER RED_HIGH_LIMIT " + hhmmss + " ");
					lastStatus=AlertColor.RED;
					lastSp02=iSpo2.toString();
					setLastGreentime(null);
					return new AlertColorAndSound(AlertColor.RED, true, hhmmss, iSpo2.toString(),puls);
				}
				}
			if (iSpo2.intValue() <= decisionParams.ORANGE_HIGH_LIMIT) {
				LOGGER.log(Level.INFO, "SMALLER ORANGE_HIGH_LIMIT " + hhmmss + " ");
				lastStatus=AlertColor.YELLOW;
				lastSp02=iSpo2.toString();
				setLastGreentime(null);
				return new AlertColorAndSound(AlertColor.YELLOW, true, hhmmss, iSpo2.toString(),puls);

			}
			if (iSpo2.intValue() >  decisionParams.ORANGE_HIGH_LIMIT) {
//				if(iSpo2.intValue() == 100){
//					if( !last_is_100){
//						hundred_since = new Date().getTime();
//					}
//					long now = new Date().getTime();
//					if( now - hundred_since > MAX100_FOR_SECONDS  *1000 ){
//						hundred_since = new Date().getTime();
//					}
//					last_is_100=true;
//					
//				}
				LOGGER.log(Level.INFO, "GREATER ORANGE_HIGH_LIMIT " + hhmmss + " ");
				lastStatus=AlertColor.GREEN;
				lastSp02=iSpo2.toString();
				setLastGreentime(currentN560Date);
				return new AlertColorAndSound(AlertColor.GREEN, false, hhmmss, iSpo2.toString(),puls);

			}
			// should not happen
			return new AlertColorAndSound(AlertColor.RED, true, "???", "???","???");
		}
	}



	private boolean lastGreentimeSecondsAgo(int seconds, Date n560Date) {
		if (lastGreentime == null) {
			return false;
		}
		long currenttime = new Date().getTime();
		long lastgoodtime = getLastGreentime().getTime();
		Date lgd = new Date(lastgoodtime);
		
		long diff = currenttime - lastgoodtime;
		System.out.println("lastgoodtime=" + lgd+ "milli="+diff);
		if (diff > 0 && diff < seconds * 1000) { // diff lt seconds *x
			return true;
		}
		return false;
	}

	private Date getLastGreentime() {
		return lastGreentime;
	}

	private void setLastGreentime(Date lastGreentime) {
		this.lastGreentime = new Date();// system date when Data arrives
	}
}
