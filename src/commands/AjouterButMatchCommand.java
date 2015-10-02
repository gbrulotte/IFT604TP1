package commands;

import java.util.List;

import matchServer.Goal;
import matchServer.Match;

public class AjouterButMatchCommand implements ICommand {

	Match match;
	Goal goal;
	
	public AjouterButMatchCommand(Match match, Goal goal) {
		this.match = match;
		this.goal = goal;
	}

	@Override
	public void execute() {
		if(match.teamA.equals(goal.team))
			match.scoreA++;
		else if(match.teamB.equals(goal.team))
			match.scoreB++;
		else
			return;
		match.goals.add(goal);
	}
}
