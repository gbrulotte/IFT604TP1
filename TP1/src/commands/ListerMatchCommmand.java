package commands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import matchServer.Client;
import matchServer.ListeDesMatchs;

import com.google.gson.*;

public class ListerMatchCommmand implements ICommand{
	
	DatagramSocket serverSocket = null;
	Client client = null;
	
	public ListerMatchCommmand(DatagramSocket serverSocket, Client client){
		this.serverSocket = serverSocket;
		this.client = client;
	}
	
	@Override
	public void execute(){
		System.out.println("!!!!!ListerMatchCommand!!!!!!");		Gson gson = new Gson();
		byte[] sendData = new byte[1024];
		
		if(ListeDesMatchs.matches.size() > 0)
			sendData = gson.toJson(ListeDesMatchs.matches.values()).getBytes();
		else
			sendData = gson.toJson("\r\n").getBytes();
		
		try {
			serverSocket.send(new DatagramPacket(sendData, sendData.length, client.address, client.port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}