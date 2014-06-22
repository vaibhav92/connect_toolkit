//Source file: e:\\java2\\DesignModel\\smsservice\\GSMCommandDispatcher.java

package smssystem;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

import jssc.*;

/**
@author Vaibhav
@version 2.0
 */
class GSMCommandDispatcher extends Thread implements SerialPortEventListener
{
   private static final int BUFFER_SIZE = 1024;//10 MB of pipe
   private ArrayList<GSMCommand> cmdlist;
   private Stack<MessageProcessor> m_processingstack;
   private boolean bExit;
   private GSMDevice device;
   private SerialPort sp;
   PipedInputStream m_modeminputstream;
   PipedOutputStream m_modemoutput;
   byte [] m_readbuffer;

   /**
   @param ds
   @throws java.io.IOException
   @throws java.lang.Exception
   @roseuid 4424C24F0014
    */
   public GSMCommandDispatcher(GSMDevice ds) throws IOException, Exception{
		super("GSMCommandDispather::" + ds.toString());
		cmdlist = new ArrayList<GSMCommand>();
		m_modeminputstream= new PipedInputStream(BUFFER_SIZE); 
		m_modemoutput = new PipedOutputStream(m_modeminputstream);
		m_readbuffer =  new byte[BUFFER_SIZE]; // allocate the buffer of same size
		device = ds;
		cmdlist=new ArrayList<GSMCommand>();
		m_processingstack=new Stack<MessageProcessor>();
		sp = new SerialPort(ds.getPort());
		sp.openPort();
		sp.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		sp.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN
				| SerialPort.FLOWCONTROL_XONXOFF_OUT);
		sp.addEventListener(this);
		System.out.println("**************************");
		setDaemon(true);
		setPriority(Thread.MIN_PRIORITY);

   }
   
   /* Pushes an entry to the processing stack */
   void pushToProcessingStack(MessageProcessor processor) {
		   m_processingstack.push(processor);
   }
   

	/**
	 * @roseuid 4424C2940078
	 */
	public void run() {
		/* MessageProcessor processor = null; */
		int readbuffer_index = 0, consumed = 0, data_available = 0;
		while (!bExit) {
			/* Fetch the availalble data first */
			try {
				data_available = m_modeminputstream.available();
			} catch (Exception e) {
				System.out.println("Unable to calculate available data " + e.toString());
				try {Thread.sleep(1000);} catch (Exception ee) {}
				continue;
			}
			/* Pass the data to the current processor */
			if (!m_processingstack.isEmpty()) {
				try {
					/* get the current processor */
					MessageProcessor processor = m_processingstack.peek();
					/* if some data available then read it */
					if (data_available > 0) {
						int space_left = m_readbuffer.length - readbuffer_index, read;
						int datatocopy = ((data_available < space_left) ? data_available : space_left);
						read = m_modeminputstream.read(m_readbuffer, readbuffer_index, datatocopy);
						/* Check for end/empty of stream */
						if (read <= 0) {
							if (read < 0) bExit = true; /* Check for error */
							continue;
						}
						/* Update the stream index counter */
						readbuffer_index += read;
					}
					/* initially no bytes are consumes */
					consumed = 0;
					/* Do we need to send lines to this command */
					if (processor.getMode() == MessageProcessor.LINE_MODE) {
						int index;
						/* look for line separator '\r\n' inside the buffer */
						for (index = 1; index < readbuffer_index; ++index) {
							if (m_readbuffer[index] == '\n' && m_readbuffer[index - 1] == '\r')
								break;
						}
						/* Check if a line was found */
						if (index >= readbuffer_index) {
							/* Still not enough data for a line */
							Thread.sleep(1000);
							/* Wait for some more data to come in */
							continue;
						}
						/* Found a line ask the processor to process it */
						System.out.println("Received a line " + new String(m_readbuffer, 0, index - 1));
						m_processingstack.pop();
						consumed = processor.processLine(this, m_readbuffer, 0, index - 1);
						assert(consumed<index);
						/*
						 * check if entire line was consumed. If yes then we
						 * have consumed two more bytes
						 */
						if (consumed == (index - 1)) {
							consumed = index + 1;
						}

					} else {
						/*
						 * Char mode then simply pass the entire buffer to the
						 * processor
						 */
						m_processingstack.pop();
						consumed = processor.processLine(this, m_readbuffer, 0, readbuffer_index);
					}
					/* update the array is some data is consumed */
					if (consumed > 0) {
						System.arraycopy(m_readbuffer, consumed, m_readbuffer, 0, readbuffer_index - consumed);
						readbuffer_index -= consumed;
					}
					/* restart the parsing */
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}

			} else if (data_available > 0) { /*
											 * Check for unsolicited responses
											 */
				/* push a new UnsolicitedResponse to the processing stack */
				pushToProcessingStack(new UnsolicitedReponseCommand(device));
				continue;
			} else {
				/* issue a command if available */
				GSMCommand cmd;
				/* reset the index */
				readbuffer_index = 0;
				synchronized (cmdlist) {
					try {
						if (cmdlist.isEmpty()) { /* Empty command list */
							System.out.println("Going to sleep");
							cmdlist.wait(); /* Wait for event */
							continue; /* Go back to the start of the loop */
						} else {
							cmd = cmdlist.remove(0); /* Get the new command */
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue; /* Go back to the start of the loop */
					}
				}
				/* Issue the command to the modem */
				byte[] cmdbytes = cmd.getCommand();
				if (cmdbytes == null) {
					System.out.println("Null command output");
					continue;
				}
				/* copy the cmd-bytes to the read buffer */
				System.arraycopy(cmdbytes, 0, m_readbuffer, 0, cmdbytes.length);
				m_readbuffer[cmdbytes.length] = '\r';
				m_readbuffer[cmdbytes.length + 1] = '\n';
				try {
					writeToSerialPort(m_readbuffer, 0, cmdbytes.length + 2);
					/*
					 * push this command to the processing stack so that it
					 * recives its output
					 */
					pushToProcessingStack(cmd);
				} catch (Exception e) {
					System.out.print("Unable to issue Command " + new String(cmdbytes));
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param cmd
	 * @roseuid 4424C2B103AC
	 */
	public void addCommand(GSMCommand cmd) {
		synchronized (cmdlist) {
			cmdlist.add(cmd);
			cmdlist.notify();
		}
		System.out.println("Added Command " + new String(cmd.getCommand()));
	}

	/**
	 * @roseuid 4424C2D8000A
	 */
	public void shutDown() {
		bExit = true;
		synchronized (cmdlist) {
			cmdlist.notify();
		}
	}

	/**
	 * @param arg0
	 * @roseuid 4424CAA002F8
	 */
	public void serialEvent(SerialPortEvent arg0) {
		try {
			switch (arg0.getEventType()) {
			case (SerialPortEvent.RXCHAR): {/* some data is available */
				/* Fetch the data from the port */
				byte[] data = sp.readBytes(arg0.getEventValue());
				m_modemoutput.write(data, 0, data.length);
				System.out.println("Data Arrival:"
						+ new String(data, "ISO-8859-1"));
				m_modemoutput.flush();
				/* Notify the cmd processor thread aboute it */
				synchronized (cmdlist) {
					cmdlist.notify();
				}
			}break;
			case SerialPortEvent.CTS:
				synchronized (cmdlist) {
					cmdlist.notify();
				}break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
   
   /* Writes a string to the serial port */
   public void writeToSerialPort(String str) throws Exception {
	   byte []  brr = str.getBytes("ISO-8859-1");
	   writeToSerialPort(brr);
   }
   
   /* Writes a byte array to the serial port */
   public void writeToSerialPort(byte [] barr) throws Exception {
	   writeToSerialPort(barr,0,barr.length);
   }
   
   /* Writes a string to the serial port */
   public void writeToSerialPort(byte [] barr, int offset, int length) throws Exception {
   	   synchronized(sp) {
   		   for(int index=0;index<length;++index) {
			   sp.writeByte(barr[offset+index]);
			   Thread.sleep(1);
		   }
	   }
   }
}