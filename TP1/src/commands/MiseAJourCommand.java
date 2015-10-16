package commands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

import com.google.gson.Gson;

import matchServer.Client;
import matchServer.ListeDesMatchs;
import matchServer.Match;

public class MiseAJourCommand implements ICommand {

	DatagramSocket serverSocket = null;
	Client client = null;
	UUID matchId;
	
	public MiseAJourCommand(DatagramSocket serverSocket, Client client, UUID matchId){
		this.serverSocket = serverSocket;
		this.client = client;
		this.matchId = matchId;
	}
	
	@Override
	public void execute() {
		System.out.println("!!!MiseAJour!!!");
		Gson gson = new Gson();
		byte[] sendData = new byte[1024];
		Match match = ListeDesMatchs.matches.get(matchId);
		if(match != null)
			sendData = gson.toJson(match).getBytes();
		try {
			serverSocket.send(new DatagramPacket(sendData, sendData.length, client.address, client.port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
