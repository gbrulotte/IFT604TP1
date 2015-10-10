package parisServer;

import java.io.Serializable;
import java.net.Socket;
import java.util.UUID;

public class ParisImp implements Serializable{
	public transient Socket socket;
	public UUID matchId;
	public int amount;
	public String team;
	
	public ParisImp(Socket socket, UUID matchId, String team, int amount){
		this.socket = socket;
		this.matchId = matchId;
		this.team = team;
		this.amount = amount;
	}
}
