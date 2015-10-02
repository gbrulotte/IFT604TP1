package matchServer;

public class Penalty {
	public String player;
	public String infringement;
	public int time;
	
	public Penalty(String player, String infringement, int time){
		this.player = player;
		this.infringement = infringement;
		this.time = time;
	}
}
