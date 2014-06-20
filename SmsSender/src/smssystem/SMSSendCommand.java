//Source file: e:\\java2\\DesignModel\\smsservice\\SMSProcessor.java

package smssystem;

import java.io.OutputStreamWriter;

/**
@author Vaibhav
@version 2.0
 */
public class SMSSendCommand extends GSMCommand
{
private byte[] arr;
private int parr=0;
private boolean flag=false;
private int lastchar=0;
private StringBuffer buff=new StringBuffer();
   /**
   @param msg
   @roseuid 4424C9B30028
    */
   public SMSSendCommand(SMSMessage msg)
   { super("at+cmgs=\"" +msg.getPhoneNumber().trim()+"\"");
     arr=msg.getMessage().getBytes();
     parr=0;
   }

   /**
   @param dat
   @param os
   @return smsservice.GSMCommand
   @roseuid 4424E348008C
    */
   public MessageProcessor processLine(byte[] dat, OutputStreamWriter os)
   {
   //System.out.println("SMSSEND--");
   //System.out.print("{");
   //System.out.print((int)dat[0]);
   //System.out.println(","+dat[0]+"}");
    
    if(flag==false)
        { if(lastchar=='>' && dat[0]==' ')
            try{ System.out.println("Sending Data");
                for(;parr< arr.length;parr++)
                   {
                    if(arr[parr]=='\r' && parr<arr.length && arr[parr+1]=='\n')
                                {os.write('\r');
                                 os.write('\n');
                                 //System.out.println("Wrote <newline>");
                                 parr+=2;
                                 break;
                                }
                    //System.out.println("Wrote :"+arr[parr]);
                    os.write(arr[parr]);
                   }
                if(parr>=arr.length) 
                    {os.write(26);flag=true;
                    System.out.println("Writing Complete waiting for reply:");
                    }
                os.flush();
            }catch(Exception e){e.printStackTrace();}
        lastchar=dat[0];
        return this;  
        }
    
    if(lastchar=='\r' && dat[0]=='\n') //line complete
        { String st=buff.toString();
          System.out.println(st);
          if(st.startsWith("+CMGS:"))
            {lastchar=0;
             buff.delete(0, buff.length());
             return this;
            }
          if(st.startsWith("+CMS"))
            {System.out.println("Error  Sending Message: "+new String(arr));
            return null;}
          if(st.startsWith("OK"))
              {System.out.println("Successfully Sent Message: "+new String(arr));
               return null;
              }
          buff.delete(0,buff.length());
          return this;
        }
    else
     if(dat[0]!='\r')   buff.append((char)dat[0]);
    lastchar=dat[0];
    return this;
   }

    public int getMode() 
        {return CHAR_MODE;}
}
