import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;

import javax.comm.SerialPort;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class DataSourceLoadCell implements ListenerLoadCell {
	public static final boolean debug=false;
	
	public String outPrefix;


	private long nPackets=0;
	
	/* GUI stuff */
	public static final boolean gui=true;
	protected JLabel labelCurrentValue;
	protected JLabel labelNPackets;
	
	/* fire it off via TCP/IP */
	public void packetReceivedLoad(int[] rawBuffer) {
		nPackets++;
		
		StringBuilder sb=new StringBuilder();
		
		for ( int i=0 ; i<rawBuffer.length ; i++ ) {
			if ( ' ' == rawBuffer[i] )
				continue;
			
			sb.append( (char) (rawBuffer[i] & 0xff) );
		}
		
		
		//System.out.println("# We received (and trimmed) -> '" + sb.toString() + "'");
		System.out.println(outPrefix + sb.toString());
		
		
		
		if ( gui ) {
			labelCurrentValue.setText(sb.toString() + " pounds");
			labelNPackets.setText(nPackets + " total packets");
		}
		
		
	}
	
	protected void setupGUI() {
		JFrame frame = new JFrame("DataSourceLoadCell");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		labelCurrentValue = new JLabel("Waiting for data...");
		labelCurrentValue.setFont(new Font("Serif", Font.PLAIN, 64));
		frame.getContentPane().add(labelCurrentValue);

		labelNPackets = new JLabel("Waiting for data...");
		labelNPackets.setFont(new Font("Serif", Font.PLAIN, 64));
		frame.getContentPane().add(labelNPackets);

		//Display the window.
		frame.setLayout(new FlowLayout());
		frame.setMinimumSize(new Dimension(400,200));
		frame.pack();
		
		frame.setVisible(true);
	}

	public void run(String[] args) throws IOException {
		String serialPortName="/dev/ttyUSB0";
		String prefix="L";
	
		if ( args.length >= 1 ) {
			serialPortName=args[0];
		}
		
		if ( args.length >= 2 ) {
			prefix=args[2];
		}
		
		outPrefix=prefix;
		
		System.err.println("# connecting to serial port " + serialPortName);
		System.err.println("# prefixing data with '" + prefix + "' before sending to DataGS");
		SerialReaderLoadstar ser810W = new SerialReaderLoadstar(serialPortName, 230400, SerialPort.PARITY_NONE);

		if ( gui ) {
			setupGUI();
		}

		
		ser810W.addPacketListener(this);
		ser810W.send("\r\n"); // end any running on loadstar

		ser810W.send("WC\r\n"); // start continuous streaming
	}

	public static void main(String[] args) throws IOException {
		System.err.println("# Major version: 2014-11-21 (precision)");
		System.err.println("# java.library.path: " + System.getProperty( "java.library.path" ));

		DataSourceLoadCell d = new DataSourceLoadCell();
		d.run(args);
	}




}
