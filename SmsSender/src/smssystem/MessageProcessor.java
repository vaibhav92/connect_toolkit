//Source file: e:\\java2\\DesignModel\\smsservice\\MessageProcessor.java

package smssystem;

import java.io.OutputStreamWriter;

/**
@author Vaibhav
@version 2.0
 */
public interface MessageProcessor
{
   final int CHAR_MODE=0;
   final int LINE_MODE=1;
   /**
   @param dat
   @param os
   @return smsservice.MessageProcessor
   @roseuid 4424BECF00BE
    */
   public MessageProcessor processLine(byte[] dat, OutputStreamWriter os);
   public int getMode();
}