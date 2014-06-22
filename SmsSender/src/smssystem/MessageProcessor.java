//Source file: e:\\java2\\DesignModel\\smsservice\\MessageProcessor.java

package smssystem;

/**
 * @author Vaibhav
 * @version 2.0
 */
abstract public class MessageProcessor {
	/* The GSM device instance */
	private GSMDevice m_device;

	public static final int CHAR_MODE = 0;
	public static final int LINE_MODE = 1;

	/* Constructor */
	protected MessageProcessor(GSMDevice device) {
		m_device = device;
	}

	/* returns the GSMDevice instance */
	protected GSMDevice getGSMDevice() {
		return m_device;
	}

	/* gets the decoded string */
	protected String decodeString(byte [] buffer, int offset, int length) {
		String resp;
		try {
			resp = new String(buffer, offset,length, "ISO-8859-1");
		} catch(Exception e){
			e.printStackTrace();
			resp = new String(buffer,offset,length);
		}
		return resp;
	}
	
	/* gets the decoded string */
	protected String decodeString(byte [] buffer) {
		return decodeString(buffer,0,buffer.length);
	}

	/* Asks the dispatcher send this data to the unsolicitedreponse handler */ 
	protected void pushBackUnsolicited(GSMCommandDispatcher dispatcher) {
		/* First push ourselves */
		dispatcher.pushToProcessingStack(this);
		dispatcher.pushToProcessingStack(new UnsolicitedReponseCommand(
				m_device));
	}
	/**
	 * @param dat
	 * @param os
	 * @return smsservice.MessageProcessor
	 * @roseuid 4424BECF00BE
	 */
	abstract public int processLine(GSMCommandDispatcher dispatcher, byte[] dat, int offset, int length);

	abstract public int getMode();
}