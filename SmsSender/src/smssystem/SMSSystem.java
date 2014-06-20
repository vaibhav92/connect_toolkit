/*
 * SMSSystem.java
 *
 * Created on August 22, 2007, 8:00 PM
 */

package smssystem;

/**
 *
 * @author  Administrator
 */
public class SMSSystem {
private static GSMDevice dev=null;
    
    /** Creates a new instance of SMSSystem */
    public static void Initialize(String prt) throws Exception
        { dev=new GSMDevice(prt);
        }
    public static GSMDevice getDevice()
        { if(dev==null)
               throw new RuntimeException("SMSDevice is not initialized");
          else return dev;
        }
   public static void unInitialize()
    {if(dev!=null)
               throw new RuntimeException("SMSDevice is not initialized");
     dev.close();
     dev=null;
    }
   
}
