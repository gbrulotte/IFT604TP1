package parisServer;

import java.io.Serializable;

public class Result implements Serializable {
	public double amount;
	public boolean isWinner;
	
	public Result(boolean isWinner){
		this.amount = 0;
		this.isWinner = isWinner;
	}
}
