package matchServer;

import java.util.List;

public class Goal {
	String team;
	String player;
	List<String> assists;
	
	public Goal(String team, String player, List<String> assists)
	{
		this.team = team;
		this.player = player;
		this.assists = assists;
	}
}
