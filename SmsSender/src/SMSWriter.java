/*
 * SMSWriter.java
 *
 * Created on July 27, 2006, 4:02 PM
 */

/**
 *
 * @author  Administrator
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import smssystem.SMSMessage;

public class SMSWriter extends Thread{
    BufferedReader bfr;
    Socket skt;
    /** Creates a new instance of SMSWriter */
    public SMSWriter(Socket skt) {
     try{
         this.skt=skt;
         bfr=new BufferedReader(new InputStreamReader(skt.getInputStream()));
      }catch(Exception e){e.printStackTrace();}
       this.start();
    }
    
    public void run() {
     String line=null;
     String ph=null;
     String msg=null;
    
        try{
            while(true)
            {line=bfr.readLine();
             if(line==null)break;
             int i=line.trim().indexOf(" ");
             ph=line.substring(0,i);
             msg=line.substring(i+1);             
             Main.gm.sendMessage(new SMSMessage(ph,msg));
            }
       }catch(Exception e){e.printStackTrace();}
        finally{
       try{
           bfr.close();
           skt.close();
          }catch(Exception ioe){}
        }
       
       }
     
    }
    

