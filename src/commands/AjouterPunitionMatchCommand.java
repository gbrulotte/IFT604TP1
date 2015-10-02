package commands;

import matchServer.Match;
import matchServer.Penalty;

public class AjouterPunitionMatchCommand implements ICommand {
	Match match;
	Penalty penalty;
	
	public AjouterPunitionMatchCommand(Match match, Penalty penalty) {
		this.match = match;
		this.penalty = penalty;
	}

	@Override
	public void execute() {
		penalty.time = match.chrono.get();
		match.penalties.add(penalty);
	}
}
