//Source file: e:\\java2\\DesignModel\\smsservice\\GSMCommandDispatcher.java

package smssystem;

import javax.comm.SerialPortEventListener;
import javax.comm.SerialPort;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import javax.comm.SerialPortEvent;
import java.util.ArrayList;

/**
@author Vaibhav
@version 2.0
 */
class GSMCommandDispatcher extends Thread implements SerialPortEventListener
{
   private ArrayList cmdlist;
   private boolean bExit;
   private InputStreamReader is;
   private OutputStreamWriter os;
   private GSMDevice device;
   private SerialPort sp;

   /**
   @param ds
   @throws java.io.IOException
   @throws java.lang.Exception
   @roseuid 4424C24F0014
    */
   public GSMCommandDispatcher(GSMDevice ds) throws IOException, Exception
   {super("GSMCommandDispather::"+ds.toString());
    int rd,flag=0;
    cmdlist=new ArrayList();
    device=ds;
    sp=ds.getPort();
    os = new OutputStreamWriter(sp.getOutputStream(), "ISO-8859-1");
    os.write("ate\r");
    os.flush();
    is = new InputStreamReader(sp.getInputStream(), "ISO-8859-1");
    for(rd=is.read(),flag=0;flag<4; rd=is.read())
            try{if(rd=='O') flag++;
            if(rd=='K' && flag==1) flag++;
            if(rd==13 && flag==2) flag++;
            if(rd==10 && flag==3) break;
            Thread.sleep(100);
           }catch(Exception e){e.printStackTrace();return;}
    System.out.println("**************************");
    sp.addEventListener(this);
    sp.notifyOnDataAvailable(true);
    sp.notifyOnOverrunError(true);
    sp.notifyOnCTS(true);
    setPriority(Thread.MIN_PRIORITY);
    start();
   }

   /**
   @roseuid 4424C2940078
    */
   public void run()
   {java.util.Stack stk=new java.util.Stack();
    java.io.ByteArrayOutputStream bos=new  java.io.ByteArrayOutputStream();
    boolean cflag=false; //charmode flag;
    GSMCommand cmd=null;
    try{outer:
        while(!bExit)
           {//if(stk.isEmpty()) 
            cmd=null;
            synchronized(cmdlist)
                    { if(/*cmd!=null||*/cmdlist.size()<=0)
                            try{System.out.println("Going to sleep");
                                cmdlist.wait();
                                if(cmdlist.size()>0)
                                    cmd=(GSMCommand)cmdlist.remove(0);
                            System.out.println("Back from sleep");
                            }catch(Exception e){e.printStackTrace();}
                       else cmd=(GSMCommand)cmdlist.remove(0);
                    }
             if(bExit) continue;
             stk.clear();
             cflag=false;  // default to line mode
             if(cmd==null) stk.push(device);
             else { System.out.println("Dispatching "+cmd.getCommand());
                    os.write(cmd.getCommand());
                    os.write('\r');
                    os.flush();
                    if(cmd.getMode()==MessageProcessor.CHAR_MODE)
                        {System.out.println("Switching to Char Mode");
                        cflag=true;
                        }   
                    stk.push(cmd);
                  }
             int lastchar=0;
             int rd=0;
             //byte buffer[]=new byte[128];int totread;
             //String st= "";
             System.out.println("entring main loop");
             for(int maxcount=0;!stk.empty();maxcount++)
                    {  try{Thread.sleep(100);} catch(Exception e){}
                      /****************************************/
                      if(maxcount>=100) //break;
                        synchronized(cmdlist)
                            { System.out.println("Entering Sleep due to unavilable data for 5 seconds");
                              try{cmdlist.wait(5000);
                                 }catch(Exception ee){ee.printStackTrace();}
                              System.out.println("Data has arrived/timeout");
                            }/**/
                      /****************************************/  
                      if(bExit) break outer;
                      MessageProcessor mp=(MessageProcessor)stk.peek();
                      while(is.ready())
                            { maxcount=0;
                              try{Thread.sleep(10);} catch(Exception e){}
                              rd=is.read();
                              if(cflag) 
                                {bos.write(rd);
                                 break;
                                 }
                              if(lastchar=='\r')
                                    if(rd=='\n')
                                        {lastchar='\n';
                                         break;
                                        }
                                    else
                                      bos.write('\r');
                               if(rd!='\r') bos.write(rd);
                               lastchar=rd;
                            }
                      
                           if(lastchar!='\n' &&cflag==false) continue;
                           if(bos.size()<=0) continue;
                           byte []arr=bos.toByteArray();
                           //System.out.println("Processing :"+new String(arr));
                           /*is this a message arrival*/
                           if(arr.length>4 && arr[0]=='+' && arr[1]=='C' && arr[2]=='M' && arr[3]=='T' && arr[4]==':')
                             mp=device.processLine(arr,os);
                           else
                             mp=mp.processLine(arr,os);
                           stk.pop();
                           if(mp!=null) stk.push(mp);
                           bos.reset();
                    }

           }
         is.close();
         os.close();
        }catch(Exception e){e.printStackTrace();}
   }

   /**
   @param cmd
   @roseuid 4424C2B103AC
    */
   public void addCommand(GSMCommand cmd)
   {  synchronized(cmdlist)
            { cmdlist.add(cmd);
              cmdlist.notify();
            } System.out.println("Added Command "+cmd.getCommand());
   }

   /**
   @roseuid 4424C2D8000A
    */
   public void shutDown()
   {  bExit=true;
      synchronized(cmdlist)
            {cmdlist.notify();}
   }

   /**
   @param arg0
   @roseuid 4424CAA002F8
    */
   public void serialEvent(SerialPortEvent arg0)
   { switch(arg0.getEventType())
        {case(SerialPortEvent.DATA_AVAILABLE):
                synchronized(cmdlist){
                           sp.notifyOnDataAvailable(true);
                           cmdlist.notify();
                           }break;
         case SerialPortEvent.CTS:
                 synchronized(cmdlist){
                                cmdlist.notify();
                            }break;
         case SerialPortEvent.OE :
                System.out.println("Buffer overRun");
                break;

       }
   }
}