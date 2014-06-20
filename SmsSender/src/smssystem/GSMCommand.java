//Source file: e:\\java2\\DesignModel\\smsservice\\GSMCommand.java

package smssystem;

import java.io.OutputStreamWriter;

/**
@author Vaibhav
@version 2.0
 */
public class GSMCommand implements MessageProcessor
{
   protected String cmd;

   /**
   @param cmd
   @roseuid 4424C363012C
    */
   public GSMCommand(String cmd)
   {this.cmd=cmd;
   }

   /**
   @return String
   @roseuid 4424BFB801FE
    */
   public String getCommand()
   {
    return cmd;
   }

   /**
   @param dat
   @param os
   @return smsservice.GSMCommand
   @roseuid 4424C9B200DC
    */
   public MessageProcessor processLine(byte[] dat, OutputStreamWriter os)
   { String st=new String(dat);
     System.out.println("Command "+ cmd+"Reply "+st);
     if(st.equals("")) return this;
     if(st.equals("OK")) return null;
     if(st.equals("ERROR")) return null;
     return this;
   }
   
   public int getMode()
   { return LINE_MODE;}
       
   
}