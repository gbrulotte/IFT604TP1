package server;

import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Match {
	String teamA;
	String teamB;
	int scoreA;
	int scoreB;
	List<Goal> goals = Collections.synchronizedList(new ArrayList<Goal>());
	List<Penalty> penalties = Collections.synchronizedList(new ArrayList<Penalty>());
	List<Socket> clients = Collections.synchronizedList(new ArrayList<Socket>());
	
	public Match(String teamA, String teamB){
		this.teamA = teamA;
		this.teamB = teamB;
	}
	
	public void addGoalA(String player, List<String> asssits){
		scoreA++;
		goals.add(new Goal(teamA, player, asssits));
	}
	
	public void addGoalB(String player, List<String> asssits){
		scoreB++;
		goals.add(new Goal(teamB, player, asssits));
	}
	
	public void addPenalties(String player, String infringement, String time){
		penalties.add(new Penalty(player, infringement, time));
	}
	
	public void addClient(Socket client){
		clients.add(client);
	}
}
