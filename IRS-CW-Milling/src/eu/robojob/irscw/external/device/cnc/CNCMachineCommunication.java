package eu.robojob.irscw.external.device.cnc;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ExternalCommunication;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.communication.SocketConnection;

public class CNCMachineCommunication extends ExternalCommunication {

	private StringBuffer command;
	private StringBuffer reply;
		
	public CNCMachineCommunication(SocketConnection socketConnection) {
		super(socketConnection);
		this.command = new StringBuffer();
		this.reply = new StringBuffer();
	}

	public void writeRegisters(int startingRegisterNr, int timeout, int[] values) throws CommunicationException, DisconnectedException {
		command = new StringBuffer();
		reply = new StringBuffer();
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
			while (extCommThread.hasNextCharacter()) {
				reply.append(extCommThread.getNextCharacter());
			}
			if (reply.toString().equals(command.toString())) {
				return;
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
		reply = new StringBuffer();
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
		List<Integer> values = new ArrayList<Integer>();
		boolean commandReached = false;
		do {
			char character = Character.UNASSIGNED;
			while (extCommThread.hasNextCharacter()) {
				character = extCommThread.getNextCharacter();
				reply.append(character);
				if (commandReached) {
					if ((character != ';') && (character != Character.UNASSIGNED)) {
						values.add(Character.getNumericValue(character));
					}
					if (values.size() == amount) {
						return values;
					}
				}
				if (reply.toString().startsWith(command.toString())) {
					commandReached = true;
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
	
	public List<Integer> readRegisters(int startingRegisterNr, int amount) throws CommunicationException, DisconnectedException {
		return readRegisters(startingRegisterNr, amount, getDefaultWaitTimeout());
	}
}
