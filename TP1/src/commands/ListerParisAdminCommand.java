package commands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import parisServer.Paris;
import matchServer.Client;
import matchServer.ListeDesMatchs;

import com.google.gson.Gson;

public class ListerParisAdminCommand implements ICommand{

	@Override
	public void execute(){
		try{
			System.out.println(Paris.paris.values());
			System.out.println(new Gson().toJson(Paris.paris.values()));
		}catch(Exception ex){
			
		}
	}
}
