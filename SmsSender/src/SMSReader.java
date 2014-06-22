/*
 * SMSReader.java
 *
 * Created on July 27, 2006, 3:03 PM
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class SMSReader extends Thread {
    
    int portno;
    ServerSocket svrskt;
    ArrayList<Socket> arrlst;
    SMSReaderService svrscr;
    /** Creates a new instance of ApplicationServers */
    public SMSReader(int portno)
    {
        try{
            
            svrskt=new ServerSocket(portno);
            arrlst=new ArrayList<Socket>();
            }catch(Exception ioe){
              System.out.println("Error: Server can not be started on port no"+portno);
              ioe.printStackTrace();
               }       
      this.start();
      svrscr=new SMSReaderService(this);
      Main.gm.addSMSReceiver(svrscr);
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
