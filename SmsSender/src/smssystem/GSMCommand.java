//Source file: e:\\java2\\DesignModel\\smsservice\\GSMCommand.java

package smssystem;

import java.io.UnsupportedEncodingException;

/**
 * @author Vaibhav
 * @version 2.0
 */
public class GSMCommand extends MessageProcessor {
	private String cmd;


	/**
	 * @param cmd
	 * @roseuid 4424C363012C
	 */
	public GSMCommand(GSMDevice device, String cmd) {
		super(device);
		this.cmd = cmd;
	}


	/**
	 * @return String
	 * @roseuid 4424BFB801FE
	 */
	public byte[] getCommand() { 
		try {
			return cmd.getBytes("ISO-8859-1");
		}catch (UnsupportedEncodingException ue) {
			ue.printStackTrace();
			return null;
		}
	}
	

	/**
	 * @param dat
	 * @param os
	 * @return smsservice.GSMCommand
	 * @roseuid 4424C9B200DC
	 */
	public int processLine(GSMCommandDispatcher dispatcher, byte[] data,
			int offset, int length) {
			String st = decodeString(data,offset,length).trim();

			if (st.equals("OK")) {
				System.out.println("Command " + cmd + "Reply " + st);
				return length;
			} else if (st.equals("ERROR")) {
				System.out.println("Command " + cmd + "Reply " + st);
				return length;
			} else if (st.length() == 0) { /*
											 * an empty line. need more lines in
											 * the future
											 */
				dispatcher.pushToProcessingStack(this);
				return length;
			} else { /* looks like an unsolicated response */
				pushBackUnsolicited(dispatcher);
				return 0;
			}
	}

	public int getMode() {
		return LINE_MODE;
	}

}