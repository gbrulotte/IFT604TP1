package server;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ListeDesMatchs {
	List<Match> matches = Collections.synchronizedList(new ArrayList<Match>());
	
	public void addMatch(Match match){
		matches.add(match);
	}
}
