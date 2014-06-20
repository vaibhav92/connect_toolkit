//Source file: e:\\java2\\DesignModel\\smsservice\\GSMDevice.java

package smssystem;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
@author Vaibhav
@version 2.0
 */
public class GSMDevice implements MessageProcessor
{
   private ArrayList arrSMSReceivers;
   private boolean isclosed;
   private SMSMessage lastmsg;
   private SerialPort sp;
   private GSMCommandDispatcher dispatcher;
   
   private static String concatenated_sms_delimiter=new String( new byte[]{(byte)0x82,(byte)0x40,(byte)0x20});
   /**
   @param strPort
   @throws java.lang.Exception
   @roseuid 4424D9BD014A
    */
   public GSMDevice(String strPort) throws Exception
   { arrSMSReceivers=new ArrayList();
      try{sp=(SerialPort)CommPortIdentifier.getPortIdentifier(strPort).open("SMSApp",2000);
          // set parameter for serial port
          sp.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

      System.out.println("open port\n");    // for debugging
         sp.setFlowControlMode(sp.FLOWCONTROL_RTSCTS_IN|sp.FLOWCONTROL_RTSCTS_OUT);
         dispatcher=new GSMCommandDispatcher(this);
         //dispatcher.addCommand(new GSMCommand("ate"));
         //dispatcher.addCommand(new GSMCommand("atz"));
         //dispatcher.addCommand(new GSMCommand("ate"));
         dispatcher.addCommand(new GSMCommand("at+cnmi=2,2,0,0"));
         dispatcher.addCommand(new GSMCommand("at+cmgf=1"));
         }catch(Exception e)
            {isclosed=true;
             throw e;
            }
   }

   /**
   @param msg
   @roseuid 44249D5500DC
    */
   public void sendMessage(SMSMessage msg)
   {
    if(isclosed) return ;
    dispatcher.addCommand(new SMSSendCommand(msg));
   }

   /**
   @param obj
   @roseuid 4424C05C015E
    */
   public void addSMSReceiver(SMSReceiver obj)
   { if(isclosed) return ;
     removeSMSReceiver(obj);
     arrSMSReceivers.add(obj);
   }

   /**
   @param obj
   @roseuid 4424C087008C
    */
   public void removeSMSReceiver(SMSReceiver obj)
   {if(isclosed) return ;
    this.arrSMSReceivers.remove(obj);
   }

   /**
   @return javax.comm.SerialPort
   @roseuid 4424C225003C
    */
   public SerialPort getPort()
   {if(isclosed) return null;
    return sp;
   }

   /**
   @roseuid 4424C2E70186
    */
   public void close()
   {if(isclosed) return ;
    isclosed=true;
    dispatcher.shutDown();
   }

   /**
   @param cmd
   @roseuid 4424E8600154
    */
   public void addCommand(GSMCommand cmd)
   {if(isclosed) return ;
    dispatcher.addCommand(cmd);
   }

   /**
   @param dat
   @param os
   @return smsservice.MessageProcessor
   @roseuid 4424ED11000A
    */
   public MessageProcessor processLine(byte[] dat, OutputStreamWriter os)
   {
    String st=new String(dat);
    System.out.println("Device Line ----- "+st);
    if(lastmsg!=null)
        {lastmsg.setMessage(st);
         System.out.println(st);
         /*Quick Fix for Concatenated SMS's*/
         //byte barr[]=st.getBytes();
         //if(barr.length()>7 && barr[0]=-126 && barr[1]==0x40 && barr[2]==0x32)
         
         if(st.startsWith(concatenated_sms_delimiter) && st.length()>7) 
            {st=st.substring(7);
             System.out.println("Concatenated SMS Detected.. Truncating to:\n "+st);
            }
         /**/
         java.util.Iterator itr= this.arrSMSReceivers.iterator();

          while(itr.hasNext())
                    {SMSReceiver sm=(SMSReceiver) itr.next();
                     sm.processSMS(lastmsg);
                     }
            lastmsg=null;
            return null;
         }
     if(st.startsWith("+CMT:"))
        { lastmsg=new SMSMessage();
          int indx= st.indexOf(',');
          if(indx!=-1)
                try{
                  String ph= st.substring(7,indx-1);
                  lastmsg.setPhoneNumber(ph);
                }catch(Exception e){e.printStackTrace();}
        return this;
        }
    lastmsg=null;
    if(st.startsWith("OK") || st.startsWith("ERROR")) return null;
    if(st.startsWith("RING"))return null;
    return this;
   }
   
   public int getMode() {
       return LINE_MODE;
   }
   
}
