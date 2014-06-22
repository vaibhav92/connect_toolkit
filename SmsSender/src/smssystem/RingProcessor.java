package smssystem;

public class RingProcessor extends MessageProcessor {
	/**
	 * @param msg
	 * @roseuid 4424C9B30028
	 */
	public RingProcessor(GSMDevice device) {
		super(device);
	}
	
	@Override
	public int processLine(GSMCommandDispatcher dispatcher, byte[] data, int offset, int length) {
		String resp = decodeString(data,offset,length).trim();
		if (resp.equals("RING")) {
				dispatcher.addCommand(new GSMCommand(getGSMDevice(),"at+chup"));
			return length;  /* consumed every thing */
		} else {
			/* some unsolicates response */
			super.pushBackUnsolicited(dispatcher);
			return length;
		}
	}
	
	@Override
	public int getMode() {
		return MessageProcessor.LINE_MODE;
	}

}
