package commands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import matchServer.Client;
import matchServer.ListeDesMatchs;

import com.google.gson.Gson;

public class ListerMatchAdminCommand implements ICommand{

	@Override
	public void execute(){
		System.out.println(new Gson().toJson(ListeDesMatchs.matches.values()));
	}
}
