package matchServer;

public class Penalty {
	public String team;
	public String player;
	public String infringement;
	public int duration;
	public int time;
	
	public Penalty(String team, String player, String infringement, int duration, int time){
		this.team = team;
		this.player = player;
		this.infringement = infringement;
		this.duration = duration;
		this.time = time;
	}
}
