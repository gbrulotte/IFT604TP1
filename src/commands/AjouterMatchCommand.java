package commands;

import matchServer.ListeDesMatchs;
import matchServer.Match;

public class AjouterMatchCommand implements ICommand {
	
	Match match = null;
	
	public AjouterMatchCommand(Match match) {
		this.match = match;
	}

	public void execute() {
		new Thread(match).start();
		ListeDesMatchs.matches.put(match.id, match);
	}
}
