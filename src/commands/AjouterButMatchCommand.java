package commands;

import java.util.List;

import matchServer.Goal;
import matchServer.Match;

public class AjouterButMatchCommand implements ICommand {

	Match match;
	String team;
	String player;
	List<String> assists;
	
	public AjouterButMatchCommand(Match match, String team, String player, List<String> assists) {
		this.match = match;
		this.team = team;
		this.player = player;
		this.assists = assists;
	}

	@Override
	public void execute() {
		match.goals.add(new Goal(team, player, assists));
	}
}
