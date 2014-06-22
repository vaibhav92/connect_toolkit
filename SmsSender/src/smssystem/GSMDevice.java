//Source file: e:\\java2\\DesignModel\\smsservice\\GSMDevice.java

package smssystem;

import java.util.ArrayList;

import jssc.*;

/**
 * @author Vaibhav
 * @version 2.0
 */
public class GSMDevice {
	private ArrayList<SMSReceiver> arrSMSReceivers;
	private boolean isclosed;
	private GSMCommandDispatcher dispatcher;
	private String m_serialport;

	/**
	 * @param strPort
	 * @throws java.lang.Exception
	 * @roseuid 4424D9BD014A
	 */
	public GSMDevice(String strPort) throws Exception {
		arrSMSReceivers = new ArrayList<SMSReceiver>();
		m_serialport = strPort;
		String[] arrports = SerialPortList.getPortNames();
		int index = 0;
		for (index = 0; index < arrports.length; ++index) {
			if (arrports[index].equals(strPort.trim()))
				break;
		}
		if (index >= arrports.length)
			throw new IllegalArgumentException("Invalid Port " + strPort);
		System.out
				.println("Creating the Command Dispatcher on Port " + strPort);
		dispatcher = new GSMCommandDispatcher(this);

		/* Schedule some standard command */
		dispatcher.addCommand(new ATECommand(this));
		dispatcher.addCommand(new GSMCommand(this, "at+cnmi=2,2,0,0"));
		dispatcher.addCommand(new GSMCommand(this, "at+cmgf=1"));

		System.out.println("Starting the dispatcher thread");
		/* Start the scheduler thread */
		dispatcher.start();
		isclosed = false;
	}

	/**
	 * @param msg
	 * @roseuid 44249D5500DC
	 */
	public void sendMessage(SMSMessage msg) {
		if (isclosed)
			return;
		dispatcher.addCommand(new SMSSendCommand(this, msg));
	}

	/**
	 * @param obj
	 * @roseuid 4424C05C015E
	 */
	public void addSMSReceiver(SMSReceiver obj) {
		if (isclosed)
			return;
		removeSMSReceiver(obj);
		arrSMSReceivers.add(obj);
	}

	/**
	 * @param obj
	 * @roseuid 4424C087008C
	 */
	public void removeSMSReceiver(SMSReceiver obj) {
		if (isclosed)
			return;
		this.arrSMSReceivers.remove(obj);
	}

	/**
	 * @return javax.comm.SerialPort
	 * @roseuid 4424C225003C
	 */
	public String getPort() {
		if (isclosed)
			return null;
		return m_serialport;
	}

	/**
	 * @roseuid 4424C2E70186
	 */
	public void close() {
		if (isclosed)
			return;
		isclosed = true;
		dispatcher.shutDown();
	}

	/**
	 * @param cmd
	 * @roseuid 4424E8600154
	 */
	public void addCommand(GSMCommand cmd) {
		if (isclosed)
			return;
		dispatcher.addCommand(cmd);
	}

	/* Method used by other classes to broadcast an event */
	void broadcastSMSReceived(SMSMessage msg) {
		for (SMSReceiver rcvr : arrSMSReceivers)
			rcvr.processSMS(msg);
	}

}
