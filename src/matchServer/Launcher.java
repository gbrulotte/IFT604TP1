package matchServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Launcher {
	private static boolean isExited = false;		
	private static final String CMD_ADDGOAL = "addGoal";
	private static final String CMD_ADDPENALTY = "addPenalty";
	private static final String CMD_SHOWMATCHLIST = "showMatchList";
	private static final String CMD_EXIT = "exit";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);		
		
		/*while (!isExited) {
			showMenu();			
			executeCommand(scanner.nextLine());
		}*/
			
		MatchServer server = new MatchServer(9000, 10);
		new Thread(server).start();

//		try {
//		    Thread.sleep(20 * 1000);
//		} catch (InterruptedException e) {
//		    e.printStackTrace();
//		}
		String input = scanner.nextLine();
		while(!input.equals("exit"));
		System.out.println("Stopping Server");
		server.stop();
		
		System.out.println("Exiting...");
	}
	
	private static void executeCommand(String command) {
		String[] splittedCommand = command.split(" ");
		String action = splittedCommand[0].toLowerCase();
				
		if (action.equalsIgnoreCase(CMD_EXIT)) {
			isExited = true;
		} else if (action.equalsIgnoreCase(CMD_ADDGOAL)) {
			addGoalCommand(splittedCommand);
		} else if (action.equalsIgnoreCase(CMD_ADDPENALTY)) {
			addPenaltyCommand(splittedCommand);
		} else if (action.equalsIgnoreCase(CMD_SHOWMATCHLIST)) {
			System.out.println("Afficher la liste...");
		} else {
			System.err.println("Command not recognized.");
		}
	}
	
	private static void addGoalCommand(String[] splittedCommand) {
		if (splittedCommand.length >= 3 && splittedCommand.length <= 5) {
			String team = splittedCommand[1];
			String player = splittedCommand[2];
			List<String> assists = new ArrayList<String>();
			for (int i = 3; i < splittedCommand.length; i++) {
				assists.add(splittedCommand[i]);					
			}
			
			if (team.equalsIgnoreCase("A") || team.equalsIgnoreCase("B")) {
				System.out.println("addGoal" + team.toUpperCase() + "(\"" + player + "\", " + assists.toString() + ");");
			} else {
				System.err.println("addGoal: Team must be 'A' or 'B'.");
			}
		} else {
			System.err.println("addGoal takes from 2 to 4 arguments.");
		}
	}
	
	private static void addPenaltyCommand(String[] splittedCommand) {
		if (splittedCommand.length == 4) {
			String player = splittedCommand[1];
			String infringement = splittedCommand[2];
			String time = splittedCommand[3];
			System.out.println("addPenalty(\"" + player + "\", \"" + infringement + "\", " + time + ");");
		} else {
			System.err.println("addPenalty takes 3 arguments.");
		}
	}
	
	private static void showMenu() {
		System.out.println("***** Hockey Server Menu *****");
		System.out.println("Command's list :");
		
		System.out.println("\t" + CMD_ADDGOAL + " team player [assists]");
		System.out.println("\t\t- team : 'A' ou 'B'");
		System.out.println("\t\t- player : The name of the player");
		System.out.println("\t\t- assists: Optional. The name of the players who assisted the scorer (maximum 2).");
		
		System.out.println("\t" + CMD_ADDPENALTY + " player infringement time");
		System.out.println("\t\t- player : The name of the player who got a penalty");
		System.out.println("\t\t- infringement : What the player did");
		System.out.println("\t\t- time : When the penalty was given");
		
		System.out.println("\t" + CMD_SHOWMATCHLIST);
				
		System.out.println("\t" + CMD_EXIT);
		System.out.println("******************************");
	}
}
