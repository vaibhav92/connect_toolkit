//Source file: e:\\java2\\DesignModel\\smsservice\\GSMCommand.java

package smssystem;

/**
 * @author Vaibhav
 * @version 2.0
 */
public class ATECommand extends GSMCommand {


	/**
	 * @param cmd
	 * @roseuid 4424C363012C
	 */
	public ATECommand(GSMDevice device) {
		super(device,"ate");
	}




	/**
	 * @param dat
	 * @param os
	 * @return smsservice.GSMCommand
	 * @roseuid 4424C9B200DC
	 */
	public int processLine(GSMCommandDispatcher dispatcher, byte[] data,
			int offset, int length) {
			String st = decodeString(data, offset, length).trim();

			if (st.equals("OK")) {
				System.out.println("Command ATE Reply " + st);
				return length;
			} else {
				dispatcher.pushToProcessingStack(this);
				return length;
			}

	}
}