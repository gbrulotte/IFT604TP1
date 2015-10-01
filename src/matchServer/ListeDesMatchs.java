package matchServer;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import commands.ICommand;

public class ListeDesMatchs extends Thread{
	public final static BlockingQueue<ICommand> queue = new ArrayBlockingQueue<ICommand>(10);
	public static List<Match> matches = Collections.synchronizedList(new ArrayList<Match>());
	
	public void addMatch(Match match){
		matches.add(match);
	}
	
	public void run(){
		try{
			ICommand command = queue.take();
			System.out.println("ListeDesMatchs: Executing command");
			command.execute();
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}
}
