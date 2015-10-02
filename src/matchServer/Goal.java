package matchServer;

import java.util.List;

public class Goal {
	public String team;
	public String player;
	public List<String> assists;
	
	public Goal(String team, String player, List<String> assists)
	{
		this.team = team;
		this.player = player;
		this.assists = assists;
	}
}
