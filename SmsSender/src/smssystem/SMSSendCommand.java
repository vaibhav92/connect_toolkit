//Source file: e:\\java2\\DesignModel\\smsservice\\SMSProcessor.java

package smssystem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
@author Vaibhav
@version 2.0
 */
public class SMSSendCommand extends GSMCommand
{
private byte[] arrbody;
private boolean flag_bodysent=false;
/**
   @param msg
   @roseuid 4424C9B30028
    */
   public SMSSendCommand(GSMDevice device, SMSMessage msg){
	 super(device, "at+cmgs=\"" +msg.getPhoneNumber().trim()+"\"");
     byte [] body=msg.getMessage().getBytes();
     arrbody = new byte[body.length+1];
     System.arraycopy(body, 0, arrbody, 0, body.length);
     arrbody[body.length] = 26; /* ^z char at the end of the body */
   }

   /**
   @param dat
   @param os
   @return smsservice.GSMCommand
   @roseuid 4424E348008C
    */
	public int processLine(GSMCommandDispatcher dispatcher,byte[] data,int offset,int length) {

		if (!flag_bodysent) { /* we havent sent the sms body yet */
			try {
				/* Create a buffered reader with all the data*/
				BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data,offset,length)));
				for(String ln=br.readLine();ln!=null;ln=br.readLine()) {
					if (ln.trim().length()==0) continue; /* skip empty lines */
					else if (ln.trim().equals(">")) { /* sms body can now be sent */
						dispatcher.writeToSerialPort(arrbody);
						flag_bodysent = true;
						System.out.println("Writing Complete waiting for reply");
					}
				}
			}catch(Exception e){
				System.out.println("Unable to process the SMS Send Command "+e.toString());
				e.printStackTrace();
			}
			/* ask dispatcher to call us back */
			dispatcher.pushToProcessingStack(this);
			return 0; /* we show nothing as consumed so that we get it back */
		} else {
			/* Start parsing the lines now */
			String resp = decodeString(data,offset,length).trim();
			if (resp.length() == 0) {
				/* ask dispatcher to call us back */
				dispatcher.pushToProcessingStack(this);
				return length; 
			} else if (resp.equals(">")) {
				/* ask dispatcher to call us back */
				dispatcher.pushToProcessingStack(this);
				return length; 
			} else if (resp.startsWith("+CMGS:")) { /* Message Sent Indication */
				/* ask dispatcher to call us back */
				dispatcher.pushToProcessingStack(this);
				return length; 
			} else if (resp.equals("OK")) { /* Transaction Complete */
				System.out.println("Message Sent Successfully");
				return length;
			} else if (resp.equals("ERROR")) { /* Transaction Complete */
				System.out.println("Unable to send the message");
				return length;
			} else { /* some unsolicated reply */
				pushBackUnsolicited(dispatcher);
				return 0;
			}
		}
	}
		

    public int getMode() 
        {return flag_bodysent?LINE_MODE:CHAR_MODE;}
}
