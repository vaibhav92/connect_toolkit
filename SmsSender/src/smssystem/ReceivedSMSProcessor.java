//Source file: e:\\java2\\DesignModel\\smsservice\\SMSProcessor.java

package smssystem;

/**
 * @author Vaibhav
 * @version 2.0
 */
public class ReceivedSMSProcessor extends MessageProcessor {
	private static String concatenated_sms_delimiter=new String( new byte[]{(byte)0x82,(byte)0x40,(byte)0x20});
	private SMSMessage m_message;
	/**
	 * @param msg
	 * @roseuid 4424C9B30028
	 */
	public ReceivedSMSProcessor(GSMDevice device) {
		super(device);
	}

	/**
	 * @param dat
	 * @param os
	 * @return smsservice.GSMCommand
	 * @roseuid 4424E348008C
	 */
	public int processLine(GSMCommandDispatcher dispatcher,byte[] data,int offset,int length) {
			String resp = decodeString(data,offset,length);
		   
		   if (m_message != null) {
			   /* Check for long messages */
			   if(resp.startsWith(concatenated_sms_delimiter))
				   resp = resp.substring(7);
			   m_message.setMessage(resp);
			   getGSMDevice().broadcastSMSReceived(m_message);
			   return length;
		   }else if (resp.startsWith("+CMT:")) {
			   m_message=new SMSMessage();
		       int indx= resp.indexOf(',');
		          if(indx!=-1) {
		        	  m_message.setPhoneNumber(resp.substring(7,indx-1));
		          }
		        dispatcher.pushToProcessingStack(this);
		        return length;
		   } else {
			   System.out.println("Invalid data "+resp);
			   return length;
		   }
	}


	@Override
	public int getMode() {
		return MessageProcessor.LINE_MODE;
	}
}
