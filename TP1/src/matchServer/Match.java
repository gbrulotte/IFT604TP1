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

import parisServer.Paris;
import commands.EnleverMatchCommand;
import commands.ICommand;
import commands.SendBetResultCommand;

public class Match implements Serializable, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int TIMER_DECREMENT_SEED = 1200;
	private static final int TIMER_INTERVAL = 30000;
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
	public transient Timer timer = new Timer();
	
	public Match(String teamA, String teamB, final UUID id) {
		this.teamA = teamA;
		this.teamB = teamB;
		this.id = id;

		timer.scheduleAtFixedRate( 
		        new TimerTask() {
		            @Override
		            public void run() {
		            	int oldValue = chrono.getAndSet(chrono.get() - TIMER_DECREMENT_SEED);
		            	if (oldValue == TIMER_DECREMENT_SEED) {
		            		try {
		            			System.out.println("The match is done.");
		            			matchDone = true;
		            			Paris.queue.put(new SendBetResultCommand(currentMatch));
		            			//ListeDesMatchs.queue.put(new EnleverMatchCommand(id));
		            		} catch (InterruptedException e) {
		            			System.out.println("Problème avec le timer du match " + id + ": " + e.toString());
		            		}
		            	}		            		
		            }
		        }, TIMER_INTERVAL, TIMER_INTERVAL);
	}

	public void run() {
		while (!matchDone) {
			try {
				ICommand command = queue.take();
				command.execute();
			} catch (InterruptedException e) {
				System.out.println("Probl?me dans la BlockingQueue de Match " + e.toString());			
			}
		}
	}

	public void addCommand(ICommand command) {
		try {
			queue.put(command);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
