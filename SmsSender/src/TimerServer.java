/*
 * ApplicationServers.java
 *
 * Created on July 26, 2006, 10:09 PM
 */
/**
 *
 * @author  Administrator
 */
import java.net.*;
import java.io.*;
import java.util.*;
public class TimerServer extends Thread {
    int portno;
    ServerSocket svrskt;
    ArrayList<Socket> arrlst;
    TimerService svrscr;
    /** Creates a new instance of ApplicationServers */
    public TimerServer(int portno)
    {
      try{
        svrskt=new ServerSocket(portno);
        arrlst=new ArrayList<Socket>();
         }catch(IOException ioe){
          System.out.println("Error: Server can not be started on port no"+portno);
          ioe.printStackTrace();
      }       
      this.start();
      svrscr=new TimerService(this);
    }
    
    public void run() {
      try{
        while(true){
            Socket skt=svrskt.accept();
            arrlst.add(skt);
        }          
      }catch(Exception e){e.printStackTrace();}
   }
 public ArrayList<Socket> getList()
 {return arrlst;}
}
