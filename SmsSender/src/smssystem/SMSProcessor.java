//Source file: e:\\java2\\DesignModel\\smsservice\\SMSProcessor.java

package smssystem;

import java.io.OutputStream;

/**
@author Vaibhav
@version 2.0
 */
public class SMSProcessor extends GSMCommand
{
   private SMSMessage mg;

   /**
   @param msg
   @roseuid 4424C9B30028
    */
   public SMSProcessor(SMSMessage msg)
   { super("at+cmgs=\"" +msg.getPhoneNumber().trim()+"\"");
     mg=msg;
   }

   /**
   @param dat
   @param os
   @return smsservice.GSMCommand
   @roseuid 4424E348008C
    */
   public MessageProcessor processLine(byte[] dat, OutputStream os)
   {try{String st=new String(dat);
    if(st.length()>=4 && st.substring(0,4).equals("+CMS"))
            {   mg.setStatus(false);
                return null;
            }
    if(st.equals(""))
             return this;
    if(st.equals("> "))
            {System.out.println("Sending Message "+ mg.getMessage());
             os.write(mg.getMessage().getBytes());
             os.write(26);
             return this;
            }
     if(st.equals("OK"))
            {System.out.println("Sent Message Successfully"+ mg.getMessage());
             mg.setStatus(true);
             return null;
            }

      }catch(Exception e)
        {e.printStackTrace();}
     return null;
   }
}