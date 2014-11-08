import java.io.IOException;

import javax.comm.SerialPort;


public class DataSourceLoadCell implements ListenerLoadCell {
	public static final boolean debug=false;

	/* fire it off via TCP/IP */
	public void packetReceivedTemperature(int[] rawBuffer) {
		StringBuilder sb=new StringBuilder();
		
		for ( int i=0 ; i<rawBuffer.length ; i++ ) {
			if ( ' ' == rawBuffer[i] )
				continue;
			
			sb.append( (char) (rawBuffer[i] & 0xff) );
		}
		
		
		System.out.println("# We received (and trimmed) -> '" + sb.toString() + "'");
	}

	public void run(String[] args) throws IOException {
		String serialPortName="/dev/ttyUSB0";
	
		if ( args.length >= 1 ) {
			serialPortName=args[0];
		}
		
		System.err.println("# connecting to serial port " + serialPortName);
		SerialReaderLoadstar ser810W = new SerialReaderLoadstar(serialPortName, 230400, SerialPort.PARITY_NONE);

		ser810W.addPacketListener(this);

	}

	public static void main(String[] args) throws IOException {
		System.err.println("# Major version: 2014-11-07 (precision)");
		System.err.println("# java.library.path: " + System.getProperty( "java.library.path" ));

		DataSourceLoadCell d = new DataSourceLoadCell();
		d.run(args);
	}




}