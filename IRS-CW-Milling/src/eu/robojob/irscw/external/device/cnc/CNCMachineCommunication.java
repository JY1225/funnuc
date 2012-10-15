package eu.robojob.irscw.external.device.cnc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ExternalCommunication;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.communication.SocketConnection;

public class CNCMachineCommunication extends ExternalCommunication {

	private StringBuffer command;
	private StringBuffer reply;
		
	private static final Logger logger = Logger.getLogger(CNCMachineCommunication.class);
	
	public CNCMachineCommunication(SocketConnection socketConnection) {
		super(socketConnection);
		this.command = new StringBuffer();
		this.reply = new StringBuffer();
	}

	public void writeRegisters(int startingRegisterNr, int timeout, int[] values) throws CommunicationException, DisconnectedException {
		command = new StringBuffer();
		command.append('W');
		command.append('W');
		if (startingRegisterNr >= 100) {
			throw new IllegalArgumentException("Register number is too high!");
		}
		if (startingRegisterNr < 10) {
			command.append(0);
		}
		command.append(startingRegisterNr);
		command.append(';');
		int amount = values.length;
		if (amount < 10) {
			command.append(0);
		}
		command.append(amount);
		command.append(';');
		for (int value : values) {
			command.append(value);
			command.append(';');
		}
		// send the command and wait for reply 
		extCommThread.clearIncommingCharacterBuffer();
		int waitedTime = 0;
		extCommThread.writeString(command.toString());
		do {
			if (extCommThread.hasNextMessage()) {
				String message = extCommThread.getNextMessage();
				if (message.equals(command.toString())) {
					return;
				}
			}
			int timeToWait = READ_RETRY_INTERVAL;
			if (timeout - waitedTime < READ_RETRY_INTERVAL) {
				timeToWait = timeout - waitedTime;
			}
			try {
				Thread.sleep(timeToWait);
			} catch(InterruptedException e) {
				break;
			}
			waitedTime += timeToWait;
		} while (waitedTime <= timeout);
		throw new ResponseTimedOutException(this);
	}
	
	public void writeRegisters(int startingRegisterNr, int[] values) throws CommunicationException, DisconnectedException {
		writeRegisters(startingRegisterNr, getDefaultWaitTimeout(), values);
	}
	
	public List<Integer> readRegisters(int startingRegisterNr, int amount, int timeout) throws CommunicationException, DisconnectedException{
		command = new StringBuffer();
		command.append('W');
		command.append('R');
		if (startingRegisterNr >= 100) {
			throw new IllegalArgumentException("Register number is too high!");
		}
		if (startingRegisterNr < 10) {
			command.append('0');
		}
		command.append(startingRegisterNr);
		if (amount >= 100) {
			throw new IllegalArgumentException("Amount number is too high!");
		}
		if (amount < 10) {
			command.append('0');
		}
		command.append(amount);
		// send the command and wait for reply 
		extCommThread.clearIncommingCharacterBuffer();
		int waitedTime = 0;
		extCommThread.writeString(command.toString());
		do {
			if (extCommThread.hasNextMessage()) {
				String response = extCommThread.getNextMessage();
				return parseResult(response, command.toString());
			}
			int timeToWait = READ_RETRY_INTERVAL;
			if (timeout - waitedTime < READ_RETRY_INTERVAL) {
				timeToWait = timeout - waitedTime;
			}
			try {
				Thread.sleep(timeToWait);
			} catch(InterruptedException e) {
				break;
			}
			waitedTime += timeToWait;
		} while (waitedTime <= timeout);
		throw new ResponseTimedOutException(this);
	}
	
	public List<Integer> parseResult(String message, String command) {
		List<Integer> results = new ArrayList<Integer>();
		if (!message.startsWith(command)) {
			throw new IllegalArgumentException("message does not start with command");
		}
		message = message.substring(command.length());
		String[] values = message.split(";");
		for (String value : values) {
			results.add(Integer.valueOf(value));
		}
		return results;
	}
	
	public boolean checkRegisterValue(int registerNumber, int value, int waitTimeout) throws CommunicationException, DisconnectedException {
		long currentTime = System.currentTimeMillis();
		List<Integer> readRegisters;
		boolean timeout = false;
		while (!timeout) {
			if (System.currentTimeMillis() - currentTime >= waitTimeout) {
				timeout = true;
				break;
			} else {
				currentTime = System.currentTimeMillis();
			}
			readRegisters = readRegisters(registerNumber, 1);
			if (readRegisters.get(0) == value) {
				return true;
			}
		}
		return false;
	}
	
	// TODO test this method
	public boolean checkRegisterValueBitPattern(int registerNumber, int bitPattern, int waitTimeout) throws CommunicationException, DisconnectedException {
		long currentTime = System.currentTimeMillis();
		List<Integer> readRegisters;
		boolean timeout = false;
		while (!timeout) {
			logger.info("checking again regiser value: " + System.currentTimeMillis() + " - " + currentTime);
			if (System.currentTimeMillis() - currentTime >= waitTimeout) {
				logger.error("time out!");
				timeout = true;
				break;
			}
			readRegisters = readRegisters(registerNumber, 1);
			if ((readRegisters.get(0) & bitPattern) == bitPattern) {
				return true;
			}
		}
		return false;
	}
	
	public List<Integer> readRegisters(int startingRegisterNr, int amount) throws CommunicationException, DisconnectedException {
		return readRegisters(startingRegisterNr, amount, getDefaultWaitTimeout());
	}
}
