package mswindowsClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import clientReceiver.DisplayReceiver;
import interfaces.UpdateableDisplay;
import rules.AlertColor;
import rules.AlertColorAndSound;
import rules.DecisionConstants;
import sound.AePlayWave;

public class WindowsDisplay extends JPanel implements UpdateableDisplay {

	private DecisionConstants decisionParams; // may be set or changed here
	// local switch
	private boolean orangeSoundAlert = false;// could also set via user entry
	private boolean blueSoundAlert =true;
	// Graphics
	private JCheckBox orangeButton;
	private JCheckBox redButton;
	private JCheckBox backgroundButton;
	private JButton testSounds;
	private JFrame frm;
	static private boolean maximizeWindow =true;
	private AlertColorAndSound colorAndSound = null;// current color und sound

	public static void main(String[] args) {
		 if (args.length < 1) {
		 System.out.println("<DataDistributorServer> for   Values expected");
		 System.exit(-1);
		 }
		 if (args.length == 2) {
			 maximizeWindow = false;
		  }
		new WindowsDisplay().startAll(args[0]);
	}

	public void startAll(String dataDistributorServer) {
		decisionParams = new DecisionConstants();
		decisionParams.setPULSOXYSERVER_ID(dataDistributorServer);
		new DisplayReceiver(this, decisionParams).start();
		try {
			Thread.sleep(2000);// wait for initial data avail
		} catch (InterruptedException e) {
		}
		show();
	}

	public void update(AlertColorAndSound colASound) {
		this.colorAndSound = colASound; 
	}

	private Color mapColor(AlertColor alercolor) {
		if (alercolor == AlertColor.GREEN)
			return Color.GREEN;
		if (alercolor == AlertColor.YELLOW)
			return Color.YELLOW;
		if (alercolor == AlertColor.RED)
			return Color.RED;
		if (alercolor == AlertColor.BLUE)
			return Color.BLUE;
		return Color.BLACK;
	}

	// assumes the current class is called MyLogger
	private final static Logger LOGGER = Logger.getLogger(WindowsDisplay.class.getName());

	/**
	 * 
	 * @param frm
	 */
	public WindowsDisplay() {
	}

	
	 private JMenuItem items[];
	   private Color colorValues[] =
	      { Color.blue, Color.yellow, Color.red };
	
 
	   
	public void show() {
		// Graphics setup
		JFrame frm = new JFrame();
		frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frm.add(this);
		frm.pack();
		frm.setVisible(true);
		frm.toFront();

		
		
		//
	    final JPopupMenu popupMenu = new JPopupMenu();
	      items = new JMenuItem[6 ];
	 
	      // construct each menu item and add to popup menu; also
	      // enable event handling for each menu item
	      int count = 0;
	         items[ count ] = new JCheckBoxMenuItem(" RED Alarm <= " + decisionParams.RED_HIGH_LIMIT );
	         items[ count ].setSelected(true);
	         items[ count ].setEnabled(false);
	         popupMenu.add( items[ count ] );
	         items[ count ].addItemListener( (e) ->{
	             
	            } );
             
	         count++;
	         items[ count ] = new JCheckBoxMenuItem(" ORANGE Alarm <= " + decisionParams.ORANGE_HIGH_LIMIT);
	         items[ count  ].setSelected(false);
	         items[ count ].setEnabled(true);
	         popupMenu.add( items[ count ] );
	         items[ count].addItemListener( (e) ->{
	            	 if (e.getStateChange() == ItemEvent.DESELECTED) {
	     				orangeSoundAlert = false;
	     			}
	     			if (e.getStateChange() == ItemEvent.SELECTED) {
	     				orangeSoundAlert = true;
	     			}
	                   repaint();
	                   return;
	                } ); 
	         popupMenu.addSeparator();
	         count++;
	         items[ count ] = new JCheckBoxMenuItem(" BLUE  Alarm on Sensor/Server lost ");
	         items[ count  ].setSelected(true);
	         items[ count ].setEnabled(true);
	         popupMenu.add( items[ count ] );
	         items[ count].addItemListener( (e) ->{
	            	 if (e.getStateChange() == ItemEvent.DESELECTED) {
	     				blueSoundAlert = false;
	     			}
	     			if (e.getStateChange() == ItemEvent.SELECTED) {
	     				blueSoundAlert = true;
	     			}
	                   repaint();
	                   return;
	                } ); 
	         popupMenu.addSeparator();
	         count++;
	         items[ count ] = new JCheckBoxMenuItem("Dark background");
	         items[ count  ].setSelected(true);
	         items[ count ].setEnabled(true);
	         popupMenu.add( items[ count  ] );
	         items[ count].addItemListener(v -> {
	 			if (v.getStateChange() == ItemEvent.DESELECTED) {
					this.setBackground(Color.LIGHT_GRAY);
				}
				if (v.getStateChange() == ItemEvent.SELECTED) {
					this.setBackground(Color.BLACK);
				}
				  repaint();
                  return;
			});
 
	         popupMenu.addSeparator();
	         
	         popupMenu.add(new JMenuItem(new AbstractAction("Test Sounds ") {
	             public void actionPerformed(ActionEvent e) {
	            	 play ("MGUN01" + ".wav") ;
	     			try {
	     				Thread.sleep(2000);
	     			} catch (InterruptedException e1) {
	     			} 
	     			play ("household017" + ".wav") ;
	     			try {
	     				Thread.sleep(2000);
	     			} catch (InterruptedException e1) {
	     			}
	     			play ("SHUTDOWN" + ".wav") ;
	            	  repaint();
	                   return;
	             }
	         }));
	      // define a MouseListener for the window that displays
	      // a JPopupMenu when the popup trigger event occurs
	      addMouseListener(
	         new MouseAdapter() {
	            public void mousePressed( MouseEvent e )
	               { checkForTriggerEvent( e ); }
	 
	            public void mouseReleased( MouseEvent e )
	               { checkForTriggerEvent( e ); }
	 
	            private void checkForTriggerEvent( MouseEvent e )
	            {
	               if ( e.isPopupTrigger() )
	                  popupMenu.show( e.getComponent(),
	                                  e.getX(), e.getY() );
	            }
	         }
	      );
	      
	      //
		this.frm = frm;


		this.setBackground(Color.BLACK);

		Timer t = new Timer(500, e ->  {
				repaint();
			}
		);
		t.start();
	}

	/**
	 * Graphics
	 * 
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void centerString(Graphics g, Rectangle r, String s, Font font) {
		FontRenderContext frc = new FontRenderContext(null, true, true);
		Rectangle2D r2D = font.getStringBounds(s, frc);
		int rWidth = (int) Math.round(r2D.getWidth());
		int rHeight = (int) Math.round(r2D.getHeight());
		int rX = (int) Math.round(r2D.getX());
		int rY = (int) Math.round(r2D.getY());

		int a = (r.width / 2) - (rWidth / 2) - rX;
		int b = (r.height / 2) - (rHeight / 2) - rY;
		g.setFont(font);
		g.drawString(s, 0, r.y + b);
	}

	public int min(int a, int b) {
		return (a <= b) ? a : b;
	}

	/**
	 * Graphics color mapping
	 * 
	 */
	
	@Override
	public void paintComponent(Graphics v) {
		super.paintComponent(v);
		v.getClipRect();
		v.setColor(mapColor(colorAndSound.color));
		frm.toFront();
		if(maximizeWindow){
	 	frm.setExtendedState(frm.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		}
		int length = min(this.getWidth(), this.getHeight());
		Font Font2 = new Font("SansSerif", Font.PLAIN, length / 3);
		v.setFont(Font2);
		v.drawString(colorAndSound.hhmmssDisplay, (int) length / 6, length / 4);
		// SP02 print
		Font Font1 = new Font("SansSerif", Font.PLAIN, length);
		v.setFont(Font1);
		if(colorAndSound.spO2Display.length()<= 2)
		v.drawString(" " +colorAndSound.spO2Display, 0, length);
		else 
			v.drawString(colorAndSound.spO2Display, 0, length);
		Graphics2D g2d = (Graphics2D) v;
		AffineTransform defaultAt = g2d.getTransform();

		// rotates the coordinate by 90 degree counterclockwise
		AffineTransform at = new AffineTransform();
		at.rotate(-Math.PI / 2);
		g2d.setTransform(at);
		Font Font3 = new Font("SansSerif", Font.PLAIN, length / 3);
		v.setFont(Font3);
		v.setColor(Color.WHITE);
		g2d.drawString(colorAndSound.pulsDisplay, -this.getHeight(), length / 3);
		  
		playWave(colorAndSound);
	}

	static AePlayWave wave =null;
	public void playWave(AlertColorAndSound colASound) {
		String soundfile = "";
		if (wave != null){
			wave.stop();
			
		}
		if (colASound.playsound) {
			if (orangeSoundAlert) {
				if (colASound.color== AlertColor.YELLOW ) {
					soundfile = "MGUN01" + ".wav";
				}
			}
			if (colASound.color== AlertColor.RED ) {
				soundfile = "SHUTDOWN" + ".wav";
			}
			if (blueSoundAlert) {
				if (colASound.color== AlertColor.BLUE)  {
					soundfile = "household017" + ".wav";
				}
			}
			System.out.println("soundfile="+soundfile+" color="+colASound.color);
			if(!soundfile.equals("")){
				wave = play(soundfile);	
			    
		    }
		}
	}
	public AePlayWave play( String soundfile){
		   AePlayWave wav  = new AePlayWave(soundfile);	
		   wav .start();
		   return wav ;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 200);
	}

}
