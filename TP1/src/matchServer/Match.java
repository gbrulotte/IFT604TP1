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

public class Match implements Serializable, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public UUID id;
	public String teamA;
	public String teamB;
	public int scoreA;
	public int scoreB;
	public AtomicInteger chrono = new AtomicInteger(36000);	public List<Goal> goals = Collections.synchronizedList(new ArrayList<Goal>());
	public List<Penalty> penalties = Collections.synchronizedList(new ArrayList<Penalty>());
	
	public final static BlockingQueue<ICommand> queue = new ArrayBlockingQueue<ICommand>(10);

	public Match(String teamA, String teamB, final UUID id) {
		this.teamA = teamA;
		this.teamB = teamB;
		this.id = id;
		        }, 30000, 30000);

	public void run() {
		while (!matchDone) {
			try {
				ICommand command = queue.take();
				command.execute();
			} catch (InterruptedException e) {
				System.out.println("Probl?me dans la BlockingQueue de Match " + e.toString());			}
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
