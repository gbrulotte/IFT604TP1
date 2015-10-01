package commands;

import java.io.OutputStream;
import java.net.DatagramPacket;

public class ListerMatchCommmand implements ICommand{
	
	DatagramPacket client = null;
	
	public ListerMatchCommmand(DatagramPacket receivePacket){
		this.client = receivePacket;
	}
	
	public void execute(){
		//Get match from fucking sigleton and send it to fucking client
		
	}
}
