//Source file: e:\\java2\\DesignModel\\smsservice\\SMSMessageNotifee.java

package smssystem;


/**
@author Vaibhav
@version 2.0
 */
public interface SMSMessageNotifee 
{
   
   /**
   @param msg
   @roseuid 44249EF100D2
    */
   public void SMSMessageSent(SMSMessage msg);
   
   /**
   @param msg
   @roseuid 44249F07015E
    */
   public void SMSMessageFailed(SMSMessage msg);
}
