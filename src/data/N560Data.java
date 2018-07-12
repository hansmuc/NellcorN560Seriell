package data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Data recod of the N 560 Pulsoxy delivered all 2 seconds
 * 
 * @author xx
 *
 */
public class N560Data {
	private Date datetime;
	private Integer sp02;
	private Integer bpm;
	private Integer pa;
	private List<String> statusList = new ArrayList<>();

	public N560Data(String dtS, String sp02S, String bpmS, String paS, String statusS) {
		datetime.setTime(Date.parse(dtS));
		sp02 = new Integer(sp02S);
		bpm = new Integer(bpmS);
		pa = new Integer(paS);
		String splitStatus[] = statusS.split(",");

		for (String st : splitStatus) {
			if (st != null) {
				statusList.add(st);
			}
		}
	}

	/**
	 * Make a String form data datetime;sp02;bpm;statusString  and 
	 * @return the marschalled string
	 */
		public String marschall() {
			String res = "";
			try {
//				System.out.println("marschall:datetime" + datetime);			
//				System.out.println("marschall:bpm" + bpm);
//				System.out.println("marschall:pa" + pa);
//				System.out.println("marschall:statusList" + statusList);
 				String statusString = "";
				for (String s : statusList) {
					statusString += s;
					statusString += ",";
				}
				SimpleDateFormat sdf = new  SimpleDateFormat("yyyy.MM.dd HH:mm:ss",Locale.US);
				sdf.format(datetime);
				res += sdf.format(datetime) + ";";
				res += sp02 + ";";
				res += bpm + ";";
				res += pa + ";";
				res += statusString + "\n";
				//System.out.println("marschalled:"+res);	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;
		}

	public void print() {
		System.out.print("marschall:" + marschall() + "\n");

	}

	public N560Data() {
		statusList = new ArrayList<>();
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public Integer getSp02() {
		return sp02;
	}

	public void setSp02(Integer sp02) {
		this.sp02 = sp02;
	}

	public Integer getBpm() {
		return bpm;
	}

	public void setBpm(Integer bpm) {
		this.bpm = bpm;
	}

	public Integer getPa() {
		return pa;
	}

	public void setPa(Integer pa) {
		this.pa = pa;
	}

	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

}
