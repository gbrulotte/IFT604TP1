package commands;

import java.util.UUID;

import matchServer.Goal;
import matchServer.ListeDesMatchs;
import matchServer.Match;
import matchServer.Penalty;

public class AjouterPunitionListeMatchsCommand implements ICommand{
	UUID matchId;
	Penalty penalty;
	
	public AjouterPunitionListeMatchsCommand(UUID matchId, Penalty penalty) {
		this.matchId = matchId;
		this.penalty = penalty;
	}

	@Override
	public void execute() {
		try {
			Match match = ListeDesMatchs.matches.get(matchId);
			match.queue.put(new AjouterPunitionMatchCommand(match, penalty));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
