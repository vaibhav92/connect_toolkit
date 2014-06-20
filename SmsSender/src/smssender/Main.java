/*
 * Main.java
 *
 * Created on March 19, 2007, 5:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the tor.
 */

package smssender;

import java.io.*;
import java.net.*;
import smssystem.*;
import java.util.*;
/**
 *
 * @author rdravid
 */
public class Main {
    static GSMDevice gm;
    static String port="COM1";
    static String server_address="192.168.101.25";
    static int server_port=10102;
    static int retryinterval=5000;
    
    public static void main(String[] args) throws Exception
    {
        Properties prs= new Properties();
        File fl=new File("config.txt");
        fl.createNewFile();
        prs.load(new FileInputStream(fl));
        port= prs.getProperty("port","COM1");
        System.out.println("Starting SMS Server on Port "+port);
        gm=new GSMDevice(port);
        System.out.println("Starting SMS Server on Port "+port);
        server_address= prs.getProperty("server-address","192.168.101.25");
        server_port= Integer.parseInt(prs.getProperty("server-port","10102"));
        
        Socket skt= null;
        BufferedReader br= null;
        PrintStream ps= null;
        StringBuffer buffer=new StringBuffer(14);
        SMSMessage smsg=new SMSMessage();
        while(true)
        {System.out.println("Connecting to SMS Dispatcher Server at "+server_address+":"+server_port);
         try{skt= new Socket(server_address,server_port);
             br= new BufferedReader(new InputStreamReader(skt.getInputStream()));
             ps= new PrintStream(skt.getOutputStream());
         }catch(Exception e)
            {System.out.println("Error connecting to server will retry in "+(retryinterval/1000) +" seconds");
              try{Thread.sleep(retryinterval);}catch(Exception ee){}
              continue;
            }
        System.out.println("Connected to server");
        String read=br.readLine();
            if(read.startsWith("-ER"))
            try{System.out.println("Server Error: "+read);
             skt.close();
             br.close();
             ps.close();
             Thread.sleep(retryinterval);
            }catch(Exception e){e.printStackTrace();continue;}
        ps.println("next");
        ps.flush();
        for(read=br.readLine();read!=null;read=br.readLine())
            {System.out.println("*******************");
            System.out.println(read);
            if(read.startsWith("+OK"))
            {int indx= read.indexOf(' ',4);
             String body=read.substring(indx).trim();
             String phone= read.substring(4,indx).trim();
             buffer.delete(0,buffer.length());
             for(int i=0;i<phone.length();i++)
                 if(Character.isDigit(phone.charAt(i)))
                     buffer.append(phone.charAt(i));
             if(body.length()>140)
                 body=body.substring(0,141);
             smsg.setPhoneNumber(buffer.toString());
             smsg.setMessage(body);
             gm.sendMessage(smsg);
            }else
            try{Thread.sleep(retryinterval);
               }catch(Exception e){e.printStackTrace();}
            ps.println("next");
            ps.flush();
            }
        }
        }
    
}