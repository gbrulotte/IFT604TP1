package parisServer;

import java.io.Serializable;
import java.util.UUID;

public class Result implements Serializable {
	public double amount;
	public boolean isWinner;
	public UUID matchId;
	public String winnerTeam;
	public String loserTeam;
	
	public Result(boolean isWinner){
		this.amount = 0;
		this.isWinner = isWinner;
	}
}
