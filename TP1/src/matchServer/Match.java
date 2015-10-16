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
	public boolean matchDone;
	
	public AtomicInteger chrono = new AtomicInteger(3600);
	public List<Goal> goals = Collections.synchronizedList(new ArrayList<Goal>());
	public List<Penalty> penalties = Collections.synchronizedList(new ArrayList<Penalty>());
	
	public final static BlockingQueue<ICommand> queue = new ArrayBlockingQueue<ICommand>(10);

	public Match(String teamA, String teamB, final UUID id) {
		this.teamA = teamA;
		this.teamB = teamB;
		this.id = id;

		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				int oldValue = chrono.getAndSet(chrono.get() - 30);
				if (oldValue == 30) {
					try {
						matchDone = true;
						ListeDesMatchs.queue.put(new EnleverMatchCommand(id));
					} catch (InterruptedException e) {
						System.out.println("Probleme avec le timer du match "
								+ id + ": " + e.toString());
					}
				}

			}
		}, 30000, 30000);
	}

	public void run() {
		while (!matchDone) {
			try {
				ICommand command = queue.take();
				command.execute();
			} catch (InterruptedException e) {
				System.out.println("Probleme dans la BlockingQueue de Match "
						+ e.toString());
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
