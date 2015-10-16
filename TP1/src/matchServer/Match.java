package matchServer;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import commands.EnleverMatchCommand;
import commands.ICommand;
import commands.SendBetResultCommand;
import parisServer.Paris;

public class Match implements Serializable, Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public UUID id;
	public String teamA;
	public String teamB;
	public int scoreA;
	public int scoreB;
	public AtomicInteger chrono = new AtomicInteger(3600); 
	public List<Goal> goals = Collections.synchronizedList(new ArrayList<Goal>());
	public List<Penalty> penalties = Collections.synchronizedList(new ArrayList<Penalty>());
	public boolean matchDone;
	public final static BlockingQueue<ICommand> queue = new ArrayBlockingQueue<ICommand>(10);
	public Match currentMatch = this;
	//public transient List<Client> clients = Collections.synchronizedList(new ArrayList<Client>());
	
	public Match(String teamA, String teamB, final UUID id){
		this.teamA = teamA;
		this.teamB = teamB;
		this.id = id;
		
		new Timer().scheduleAtFixedRate( 
		        new TimerTask() {
		            @Override
		            public void run() {
		            	int oldValue = chrono.getAndSet(chrono.get() - 30);
		            	if(oldValue == 30){
		            		try {
		            			matchDone = true;
		            			Paris.queue.put(new SendBetResultCommand(currentMatch));
		            			//ListeDesMatchs.queue.put(new EnleverMatchCommand(id));
		            		} catch (InterruptedException e) {
		            			System.out.println("Problème avec le timer du match " + id + ": " + e.toString());
		            		}
		            	}
		            	
		            		
		            }
		        }, 30000, 30000);
	}
	
	public void addGoalA(String player, List<String> asssits){
		scoreA++;
		goals.add(new Goal(teamA, player, asssits));
	}
	
	public void addGoalB(String player, List<String> asssits){
		scoreB++;
		goals.add(new Goal(teamB, player, asssits));
	}
	
	/*public void addPenalties(String player, String infringement, String time){
		penalties.add(new Penalty(player, infringement, time));
	}
	
	public void addClient(Client client){
		clients.add(client);
	}*/
	
	public void run(){
		while(!matchDone){
			try {
				ICommand command = queue.take();
				command.execute();
			} catch (InterruptedException e) {
				System.out.println("Problème dans la BlockingQueue de Match " + e.toString());
			}
		}
		
	}
}
