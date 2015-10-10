package parisServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import commands.ICommand;

public class Paris extends Thread {
	public final static BlockingQueue<ICommand> queue = new ArrayBlockingQueue<ICommand>(10);
	public static Map<UUID, ArrayList<ParisImp>> paris = Collections.synchronizedMap(new HashMap<UUID, ArrayList<ParisImp>>());

	public void run() {
		
		while (true) {
			ICommand command;
			try {
				command = queue.take();
				command.execute();
			} catch (Exception e) {
				System.out.println("Problème avec la BlockingQueue de Paris " + e.toString());
			}
		}
	}
}
