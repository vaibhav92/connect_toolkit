/*
 * SMSReaderService.java
 *
 * Created on July 27, 2006, 3:10 PM
 */



/*
 * Service.java
 *
 * Created on July 26, 2006, 11:13 PM
 */

/**
 *
 * @author  Administrator
 */
import java.io.*;

import smssystem.*;

import java.util.*;
import java.net.*;
public class SMSReaderService extends Thread implements SMSReceiver{
  private static class Msg{
      public String msg;
      public String phone;
      
  }
    ArrayList<Msg> arr=new ArrayList<Msg>();
    private  SMSReader svr;
    /** Creates a new instance of Service */
    public SMSReaderService(SMSReader svr) {
        this.svr=svr;
        this.start();
    }
    
    public  synchronized void run() {
       
       while(true)
       synchronized(arr)
           {
               try{
                arr.wait();
               }catch(Exception eee){eee.printStackTrace();continue;}
                Iterator<Msg> itrmsg=arr.iterator();
                while(itrmsg.hasNext())
                {
                  Msg m=itrmsg.next();  
                  Iterator<Socket> itrcnt=svr.getList().iterator();
                
                  while(itrcnt.hasNext())
                  {   Socket skt=(Socket)itrcnt.next();
                      try{
                            OutputStream out=skt.getOutputStream();
                            out.write(m.phone.getBytes());
                            out.write( (byte)' ');
                            out.write(m.msg.getBytes());
                            out.write(0);
                            
                         }catch(Exception ioe)
                         {try{skt.close();} catch(Exception se){} itrcnt.remove();}
                 }
                }  
                arr.clear();
            }
       
    }
   
    
    public void processSMS(SMSMessage mesg) {
        Msg m=new Msg();
        m.phone=mesg.getPhoneNumber();
        m.msg=mesg.getMessage();
        synchronized(arr)
        {
         arr.add(m);
         arr.notifyAll();
        }
       
      }
    
}


