//Source file: e:\\java2\\DesignModel\\smsservice\\PhoneBookProcessor.java

package smssystem;

import java.io.OutputStreamWriter;

/**
@author Vaibhav
@version 2.0
 */
public class MessageReadCommand extends GSMCommand
{
 private boolean flag=false;
   /**
   @roseuid 4424DB01003C
    */
    public MessageReadCommand()
   {super("at+cmgl=\"all\"");
   }

   /**
   @param dat
   @param os
   @return smsservice.GSMCommand
   @roseuid 4424E34E0078
    */
   public MessageProcessor processLine(byte[] dat, OutputStreamWriter os)
   {
    try{String st=new String(dat);
    System.out.println(st);
    if(flag)
            { System.out.println("Message---> "+st);
              flag=false;
              return this;
            }
    if(st.equals(super.cmd)) return this;
    if(st.equals(""))
             return this;
    if(!flag&&st.startsWith("+CMGL:"))
            { //System.out.println(st);
                System.out.println();
              flag=true;
              return this;
            }
     if(st.equals("OK"))
            {System.out.println("Phone book read complete" +st);
             return null;
            }

      }catch(Exception e)
        {e.printStackTrace();}
     return this;
   }
}