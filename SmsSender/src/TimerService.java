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
import java.util.*;
import java.net.*;
public class TimerService extends Thread{
   private  TimerServer svr;
    /** Creates a new instance of Service */
    public TimerService(TimerServer svr) {
        this.svr=svr;
        this.start();
    }
    
    public  synchronized void run() {
       while(true)
       {
           ArrayList<Socket> arlst=svr.getList();
           if(arlst!=null)
           {       
           Iterator<Socket> itr=arlst.iterator();
           try{
           while(itr.hasNext())
           {   Socket skt=itr.next();
               try{
               OutputStream out=skt.getOutputStream();
               out.write("T".getBytes());
               }catch(Exception ioe){try{skt.close();} catch(Exception se){} itr.remove();}
               
           }
           Thread.sleep(1000);
           }catch(Exception e){e.printStackTrace();}
           }
       }
    }
    public void stopService()
    {
        
    }
    
}
