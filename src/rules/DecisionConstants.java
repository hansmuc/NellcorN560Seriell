package rules;

public class DecisionConstants {
	public String getPULSOXYSERVER_ID() {
		return PULSOXYSERVER_ID;
	}
	public void setPULSOXYSERVER_ID(String pULSOXYSERVER_ID) {
		PULSOXYSERVER_ID = pULSOXYSERVER_ID;
	}
	public int getPULSOXYSERVER_PORT() {
		return PULSOXYSERVER_PORT;
	}
	public void setPULSOXYSERVER_PORT(int pULSOXYSERVER_PORT) {
		PULSOXYSERVER_PORT = pULSOXYSERVER_PORT;
	}
	public int getORANGE_HIGH_LIMIT() {
		return ORANGE_HIGH_LIMIT;
	}
	public void setORANGE_HIGH_LIMIT(int oRANGE_HIGH_LIMIT) {
		ORANGE_HIGH_LIMIT = oRANGE_HIGH_LIMIT;
	}
	public int getRED_HIGH_LIMIT() {
		return RED_HIGH_LIMIT;
	}
	public void setRED_HIGH_LIMIT(int rED_HIGH_LIMIT) {
		RED_HIGH_LIMIT = rED_HIGH_LIMIT;
	}
	//connect
	public String PULSOXYSERVER_ID = "localhost";
	public int PULSOXYSERVER_PORT = 5001;
	//algorithm
	public int ORANGE_HIGH_LIMIT = 94;
	public int RED_HIGH_LIMIT = 80;
}
