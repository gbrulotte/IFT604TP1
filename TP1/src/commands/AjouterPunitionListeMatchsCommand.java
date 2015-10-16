package commands;

import java.util.UUID;
import matchServer.ListeDesMatchs;
import matchServer.Match;
import matchServer.Penalty;

public class AjouterPunitionListeMatchsCommand implements ICommand {

	UUID matchId;
	Penalty penalty;

	public AjouterPunitionListeMatchsCommand(UUID matchId, Penalty penalty) {
		this.matchId = matchId;
		this.penalty = penalty;
	}

	@Override
	public void execute() {
		Match match = ListeDesMatchs.matches.get(matchId);
		match.addCommand(new AjouterPunitionMatchCommand(match, penalty));
	}
}
