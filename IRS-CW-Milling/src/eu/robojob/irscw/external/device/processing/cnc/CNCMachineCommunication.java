package eu.robojob.irscw.external.device.processing.cnc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ExternalCommunication;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.communication.SocketConnection;

public class CNCMachineCommunication extends ExternalCommunication {

	private StringBuffer command;
		
	private static final Logger logger = Logger.getLogger(CNCMachineCommunication.class);
	private AbstractCNCMachine cncMachine;
	
	public CNCMachineCommunication(SocketConnection socketConnection, AbstractCNCMachine cncMachine) {
		super(socketConnection);
		this.cncMachine = cncMachine;
		this.command = new StringBuffer();
	}

	public synchronized void writeRegisters(int startingRegisterNr, int timeout, int[] values) throws AbstractCommunicationException, DisconnectedException {
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
		extCommThread.clearIncommingBuffer();
		int waitedTime = 0;
		extCommThread.writeString(command.toString());
		do {
			if (extCommThread.hasNextMessage()) {
				String message = extCommThread.getNextMessage();
				if (command.toString().startsWith(message)) {
					return;
				}
			}
			int timeToWait = READ_RETRY_INTERVAL;
			if (timeout - waitedTime < READ_RETRY_INTERVAL) {
				timeToWait = timeout - waitedTime;
			}
			if (timeToWait <= 0) {
				break;
			}
			try {
				Thread.sleep(timeToWait);
			} catch(InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
				break;
			}
			waitedTime += timeToWait;
		} while (waitedTime <= timeout);
		throw new ResponseTimedOutException(extCommThread.getSocketConnection());
	}
	
	public synchronized void writeRegisters(int startingRegisterNr, int[] values) throws AbstractCommunicationException, DisconnectedException {
		writeRegisters(startingRegisterNr, getDefaultWaitTimeout(), values);
	}
	
	public synchronized List<Integer> readRegisters(int startingRegisterNr, int amount, int timeout) throws AbstractCommunicationException, DisconnectedException{
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
		extCommThread.clearIncommingBuffer();
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
			if (timeToWait <= 0) {
				break;
			}
			try {
				Thread.sleep(timeToWait);
			} catch(InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
				break;
			}
			waitedTime += timeToWait;
		} while (waitedTime <= timeout);
		throw new ResponseTimedOutException(extCommThread.getSocketConnection());
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
	
	public synchronized boolean checkRegisterValue(int registerNumber, int value, int waitTimeout) throws AbstractCommunicationException, DisconnectedException {
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
	public synchronized boolean checkRegisterValueBitPattern(int registerNumber, int bitPattern, int waitTimeout) throws AbstractCommunicationException, DisconnectedException {
		long currentTime = System.currentTimeMillis();
		List<Integer> readRegisters;
		boolean timeout = false;
		while (!timeout) {
			logger.info("checking again regiser value: " + System.currentTimeMillis() + " - " + currentTime);
			if (System.currentTimeMillis() - currentTime >= waitTimeout) {
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
	
	public synchronized List<Integer> readRegisters(int startingRegisterNr, int amount) throws AbstractCommunicationException, DisconnectedException {
		return readRegisters(startingRegisterNr, amount, getDefaultWaitTimeout());
	}

	@Override
	public void connected() {
		cncMachine.processCNCMachineEvent(new CNCMachineEvent(cncMachine, CNCMachineEvent.CNC_MACHINE_CONNECTED));
	}

	@Override
	public void disconnected() {
		cncMachine.processCNCMachineEvent(new CNCMachineEvent(cncMachine, CNCMachineEvent.CNC_MACHINE_DISCONNECTED));
	}

	@Override
	public void iOExceptionOccured(IOException e) {
		// we just log the error here
		logger.error(e);
		e.printStackTrace();
	}
}
