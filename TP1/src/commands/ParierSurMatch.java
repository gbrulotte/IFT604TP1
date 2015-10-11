package commands;

import java.util.ArrayList;
import matchServer.ListeDesMatchs;
import matchServer.Match;
import parisServer.Paris;
import parisServer.ParisImp;

public class ParierSurMatch implements ICommand {

	private ParisImp paris;

	public ParierSurMatch(ParisImp paris) {
		this.paris = paris;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		Match match = ListeDesMatchs.matches.get(paris.matchId);
		if (match != null && isBetEnabled(ListeDesMatchs.matches.get(paris.matchId))) {
			if (Paris.paris.containsKey(paris.matchId)) {
				ArrayList<ParisImp> listeParis = Paris.paris.get(paris.matchId);
				listeParis.add(paris);
			} else {
				ArrayList<ParisImp> listeParis = new ArrayList<ParisImp>();
				listeParis.add(paris);
				Paris.paris.put(paris.matchId, listeParis);
			}
		}
	}

	private boolean isBetEnabled(Match match) {
		return match.chrono.get() >= 20 * 60;
	}

}
