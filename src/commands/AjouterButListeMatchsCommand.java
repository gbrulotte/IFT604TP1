package commands;

import java.util.List;
import java.util.UUID;

import matchServer.ListeDesMatchs;

public class AjouterButListeMatchsCommand implements ICommand {

	UUID matchId;
	String team;
	String player;
	List<String> assists;
	
	public AjouterButListeMatchsCommand(UUID matchId, String team, String player, List<String> assists) {
		this.matchId = matchId;
		this.team = team;
		this.player = player;
		this.assists = assists;
	}

	@Override
	public void execute() {
		try {
			ListeDesMatchs.matches.get(matchId).queue.put(new AjouterButMatchCommand(ListeDesMatchs.matches.get(matchId), team, player, assists));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
