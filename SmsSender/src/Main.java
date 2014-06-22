/*
 * Main.java
 *
 * Created on July 26, 2006, 10:21 PM
 */

/**
 *
 * @author  Administrator
 */
import java.net.ServerSocket;
import java.net.Socket;

import smssystem.GSMDevice;
public class Main 
{
public static GSMDevice gm=null;
@SuppressWarnings("unused")
private static SMSReader svr;    
    /** Creates a new instance of Main */
@SuppressWarnings("unused")
private static TimerServer svr2;
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
        String port="COM1";
        if(args.length>0)
        if(args[0].startsWith("#"))
         port=args[0].substring(1);
        else port=args[0];
        
        System.out.println("Using Port "+port);
      try{  gm=new GSMDevice(port);}catch(Exception e){e.printStackTrace(); throw e;}
        svr = new SMSReader(9021);
        svr2 = new TimerServer(9020);
        ServerSocket svrskt=null;
        try{
         svrskt=new ServerSocket(9023);
        }catch(Exception e){e.printStackTrace();}
        
        try{
         while(true)
         {
             Socket skt=svrskt.accept();
             new SMSWriter(skt);
         }
        }catch(Exception e){e.printStackTrace();}
       finally{
           try{
               svrskt.close();
           }catch(Exception ee){ee.printStackTrace();}
       }/**/
    }
    
}

