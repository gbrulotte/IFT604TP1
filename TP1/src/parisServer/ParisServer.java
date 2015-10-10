package parisServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import commands.ParierSurMatch;

public class ParisServer implements Runnable {

	protected int serverPort = 8081;
	private boolean isStopped = false;
	
	
	public ParisServer(int serverPort) {
		this.serverPort = serverPort;
	}

	public void run() {
		try {
			new Thread(new Paris()).start();
			ServerSocket serverSocket = new ServerSocket(serverPort);

			while (!isStopped()) {
				Socket socket = serverSocket.accept();

				BufferedReader in = new BufferedReader(
				        new InputStreamReader(socket.getInputStream()));
				
				String inputLine = in.readLine();
				
				System.out.println(inputLine);
				
				inputLine = inputLine.trim();
				String[] typeCommand = inputLine.split("~");
				
				try {
					switch (typeCommand[0]) {
					case "Bet":
						UUID matchId = UUID.fromString(typeCommand[1]);
						String team = typeCommand[2];
						int amount = Integer.parseInt(typeCommand[3]);
						
						ParisImp paris = new ParisImp(socket, matchId, team, amount);
						ParierSurMatch command = new ParierSurMatch(paris);
						Paris.queue.put(command);
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
			
		} catch (Exception ex) {
			System.out.println("Server stopped");
			stop();
		}
	}
	
	private synchronized boolean isStopped() {
		return isStopped;
	}

	public synchronized void stop() {
		this.isStopped = true;

	}

}
