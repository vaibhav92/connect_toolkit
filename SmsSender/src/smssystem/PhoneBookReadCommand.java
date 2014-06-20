//Source file: e:\\java2\\DesignModel\\smsservice\\PhoneBookProcessor.java

package smssystem;

import java.io.OutputStreamWriter;

/**
@author Vaibhav
@version 2.0
 */
public class PhoneBookReadCommand extends GSMCommand
{

   /**
   @roseuid 4424DB01003C
    */
    public PhoneBookReadCommand()
   {super("at+cpbr=1,40");
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
    if(st.equals(super.cmd)) return this;
    if(st.equals(""))
             return this;
    if(st.startsWith("+CPBR:"))
            { System.out.println("ReadPhoneBook "+st);
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