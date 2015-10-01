package matchServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import commands.AjouterButListeMatchsCommand;
import commands.AjouterMatchCommand;
import commands.ListerMatchAdminCommand;

public class Launcher {
	private static boolean isExited = false;		
	private static final String CMD_ADDGOAL = "addGoal";
	private static final String CMD_ADDPENALTY = "addPenalty";
	private static final String CMD_SHOWMATCHLIST = "showMatchList";
	private static final String CMD_ADDMATCH = "addMatch";
	private static final String CMD_EXIT = "exit";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);		
		
		MatchServer server = new MatchServer(8080, 10);
		new Thread(server).start();
		
		while (!isExited) {
			showMenu();
			try {
				ListeDesMatchs.queue.put(new ListerMatchAdminCommand());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			executeCommand(scanner.nextLine());
		}
		
		scanner.close();
		server.stop();
		
		System.out.println("Exiting...");
	}
	
	private static void executeCommand(String command) {
		String[] splittedCommand = command.split(" ");
		String action = splittedCommand[0].toLowerCase();
				
		if (action.equalsIgnoreCase(CMD_EXIT)) {
			isExited = true;
		} else if (action.equalsIgnoreCase(CMD_ADDMATCH)) {
			addMatch(splittedCommand);
		}else if (action.equalsIgnoreCase(CMD_ADDGOAL)) {
			addGoalCommand(splittedCommand);
		} else if (action.equalsIgnoreCase(CMD_ADDPENALTY)) {
			addPenaltyCommand(splittedCommand);
		} else if (action.equalsIgnoreCase(CMD_SHOWMATCHLIST)) {
			System.out.println("Afficher la liste...");
		} else {
			System.err.println("Command not recognized.");
		}
	}
	
	private static void addMatch(String[] splittedCommand){
		if(splittedCommand.length == 3){
			String teamA = splittedCommand[1];
			String teamB = splittedCommand[2];
			ListeDesMatchs.queue.add(new AjouterMatchCommand(new Match(teamA, teamB, UUID.randomUUID())));
			
		} else{
			System.err.println("addMatch: Must have two teams");
		}
	}
	
	private static void addGoalCommand(String[] splittedCommand) {
		if (splittedCommand.length >= 4 && splittedCommand.length <= 6) {
			UUID matchId = UUID.fromString(splittedCommand[1]);
			String team = splittedCommand[2];
			String player = splittedCommand[3];
			List<String> assists = new ArrayList<String>();
			for (int i = 4; i < splittedCommand.length; i++) {
				assists.add(splittedCommand[i]);					
			}
			ListeDesMatchs.queue.add(new AjouterButListeMatchsCommand(matchId, team, player, assists));
		} else {
			System.err.println("addGoal takes from 4 to 6 arguments.");
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
		
		System.out.println("\t" + CMD_ADDMATCH + " team team");
		
		System.out.println("\t" + CMD_ADDGOAL + " matchId team player [assists]");
		System.out.println("\t\t- position in list");
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
