package commands;

import java.util.List;
import java.util.UUID;

import matchServer.Goal;
import matchServer.ListeDesMatchs;
import matchServer.Match;

public class AjouterButListeMatchsCommand implements ICommand {

	UUID matchId;
	Goal goal;
	
	public AjouterButListeMatchsCommand(UUID matchId, Goal goal) {
		this.matchId = matchId;
		this.goal = goal;
	}

	@Override
	public void execute() {
		try {
			Match match = ListeDesMatchs.matches.get(matchId);
			match.queue.put(new AjouterButMatchCommand(match, goal));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
