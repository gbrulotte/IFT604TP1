package commands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import parisServer.Paris;
import parisServer.ParisImp;
import matchServer.Client;
import matchServer.ListeDesMatchs;

import com.google.gson.Gson;

public class ListerParisAdminCommand implements ICommand{

	@Override
	public void execute(){
		for (ArrayList<ParisImp> parisLists : Paris.paris.values()) {
			for (ParisImp parisImp : parisLists) {
				System.out.println(new Gson().toJson(parisImp));
			}
		}
	}
}
