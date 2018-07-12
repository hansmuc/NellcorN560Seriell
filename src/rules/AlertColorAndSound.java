package rules;


public class AlertColorAndSound {

		public AlertColorAndSound(AlertColor color, boolean playsound, String hhmmssDisplay, String spO2Display, String puls) {
			super();
			this.color = color;
			this.playsound = playsound;
			this.hhmmssDisplay = hhmmssDisplay;
			this.spO2Display = spO2Display;
			this.pulsDisplay=puls;
		}

		public AlertColor color;
		public boolean playsound;
		public String hhmmssDisplay;
		public String spO2Display;
		public String pulsDisplay;
		private String info = "";
		public String getInfo() {
			return info;
		}
		public void setInfo(String info) {
			this.info = info;
		}
	}
