package commands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import matchServer.ListeDesMatchs;
import matchServer.Match;

import com.google.gson.*;

public class ListerMatchCommmand implements ICommand{
	
	DatagramSocket serverSocket = null;
	DatagramPacket packet = null;
	
	public ListerMatchCommmand(DatagramSocket serverSocket, DatagramPacket packet){
		this.serverSocket = serverSocket;
		this.packet = packet;
	}
	
	public void execute(){
		Gson gson = new Gson();
		byte[] sendData = new byte[1024];
		sendData = gson.toJson(ListeDesMatchs.matches).getBytes();
		try {
			serverSocket.send(new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}