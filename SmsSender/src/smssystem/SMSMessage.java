//Source file: e:\\java2\\DesignModel\\smsservice\\SMSMessage.java

package smssystem;


/**
@author Vaibhav
@version 2.0
 */
public class SMSMessage implements SMSMessageNotifee
{
   private String msg;
   private String phone;
   private Integer sync;
   private long msgid;
   private boolean success;
   private static long mid;

   /**
   @param ph
   @param mg
   @roseuid 4424E0FB0154
    */
   public SMSMessage(String ph, String mg)
   {    this();
        this.phone=ph;
        this.msg=mg;
   }

   /**
   @roseuid 4424C3620168
    */
   public SMSMessage()
   {msgid=++mid;
   }

   /**
   @return java.lang.String
   @roseuid 44249C11021C
    */
   public String getMessage()
   {
    return msg;
   }

   /**
   @param msg
   @roseuid 44249C2702DA
    */
   public void setMessage(String msg)
   {    this.msg=msg;
   }

   /**
   @return java.lang.String
   @roseuid 44249C3601C2
    */
   public String getPhoneNumber()
   {
    return phone;
   }

   /**
   @param num
   @roseuid 44249C4D00BE
    */
   public void setPhoneNumber(String num)
   { phone=num;
   }

   /**
   @param tm
   @throws java.lang.Exception
   @roseuid 44249C5900A0
    */
   public void waitTillSent(long tm) throws Exception
   {   synchronized(sync)
            {sync.wait();
            }
   }

   /**
   @return long
   @roseuid 44249C9B02DA
    */
   public long getMessageID()
   {
    return msgid;
   }

   /**
   @param msg
   @roseuid 4424C3620212
    */
   public void SMSMessageSent(SMSMessage msg)
   {if (msgid==msg.getMessageID())
         synchronized(sync)
            {success=true;
             sync.notifyAll();
             }
   }

   /**
   @param msg
   @roseuid 4424C3620320
    */
   public void SMSMessageFailed(SMSMessage msg)
   { if (msgid==msg.getMessageID())
         synchronized(sync)
            {success=false;
             sync.notifyAll();
             }
   }

   /**
   @return boolean
   @roseuid 4424DF110082
    */
   public boolean getStatus()
   {
    return success;
   }

   /**
   @param b
   @roseuid 4424DF170186
    */
   public void setStatus(boolean b)
   {success=b;
   }
   
   public void sent(){m_issent=true;}
   public boolean isSent(){return m_issent;}
   private boolean m_issent=false;
}
