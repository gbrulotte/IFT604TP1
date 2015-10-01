package commands;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

import matchServer.Client;
import matchServer.ListeDesMatchs;

public class TraiterCommande implements Runnable {

	protected DatagramSocket serverSocket = null;
	protected DatagramPacket receivePacket = null;
	protected String input = "";

	public TraiterCommande(DatagramSocket serverSocket, DatagramPacket receivePacket, String input) {
		this.serverSocket = serverSocket;
		this.receivePacket = receivePacket;
		this.input = input;
	}

	public void run() {
		input = input.trim();
		String[] typeCommand = input.split("~");
		Client client = new Client(receivePacket.getAddress(),
				receivePacket.getPort());
		try {
			switch (typeCommand[0]) {
			case "ListerMatch":
				ListeDesMatchs.queue.put(new ListerMatchCommmand(serverSocket, client));
				break;
			/*case "SuivreMatch":
				ListeDesMatchs.queue.put(new SuivreMatchCommand(client, typeCommand[1]));
				break;*/
			case "MiseAJour":
				ListeDesMatchs.queue.put(new MiseAJourCommand(serverSocket, client, UUID.fromString(typeCommand[1])));
				break;
			default:
				System.out.println("Commande inconnue");
				break;
			}
		} catch (InterruptedException e) {
			System.out.println("Problème avec la BlockingQueue de ListeDesMatchs " + e.toString());
		} catch(IllegalArgumentException e){
			System.out.println("Argument invalide, on oublie cette commande " + e.toString());			
		}
	}
}