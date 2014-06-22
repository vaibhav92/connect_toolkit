//Source file: e:\\java2\\DesignModel\\smsservice\\SMSProcessor.java

package smssystem;

/**
 * @author Vaibhav
 * @version 2.0
 */
public class UnsolicitedReponseCommand extends MessageProcessor {

	/**
	 * @param msg
	 * @roseuid 4424C9B30028
	 */
	public UnsolicitedReponseCommand(GSMDevice device) {
		super(device);
	}

	/**
	 * @param dat
	 * @param os
	 * @return smsservice.GSMCommand
	 * @roseuid 4424E348008C
	 */
	public int processLine(GSMCommandDispatcher dispatcher,byte[] data,int offset,int length) {
		String resp = decodeString(data,offset,length).trim();

		if (resp.startsWith("+CMT")) {
			dispatcher.pushToProcessingStack(new ReceivedSMSProcessor(getGSMDevice()));
			return 0;
		} else if (resp.equals("RING")) {
			dispatcher.pushToProcessingStack(new RingProcessor(getGSMDevice()));
			return 0;
		} else if(resp.length() == 0) { /* need some more lines */
			dispatcher.pushToProcessingStack(this);
			return 0;
		}
		else {
			System.out.println("Ignoring the unsolicate string "+ resp);
			return length;	
		}
	}

	@Override
	public int getMode() {
		return MessageProcessor.LINE_MODE;
	}



}
