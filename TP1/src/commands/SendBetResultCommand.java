package commands;

import java.io.DataOutputStream;
import java.util.ArrayList;

import com.google.gson.Gson;

import matchServer.Match;
import parisServer.Paris;
import parisServer.ParisImp;
import parisServer.Result;

public class SendBetResultCommand implements ICommand {

	private enum Team {
		TeamA, TeamB
	}

	private Match match;
	private Team winner;
	private int totalAmount;
	private ArrayList<ParisImp> bettorWinners;
	private ArrayList<ParisImp> bettorLosers;
	private int winnerTotalBets;

	public SendBetResultCommand(Match match) {
		this.match = match;
		this.winner = getMatchWinnerTeam();
		this.totalAmount = 0;
		this.winnerTotalBets = 0;

		bettorWinners = new ArrayList<>();
		bettorLosers = new ArrayList<>();
	}

	@Override
	public void execute() {
		ArrayList<ParisImp> listeParis = Paris.paris.get(match.id);

		if (listeParis != null) {
			for (ParisImp paris : listeParis) {
				if (isWinner(paris)) {
					bettorWinners.add(paris);
					winnerTotalBets += paris.amount;
				} else {
					bettorLosers.add(paris);
				}
				totalAmount += paris.amount;
			}
		}

		sendResult(bettorWinners, true);
		sendResult(bettorLosers, false);

	}

	private void sendResult(ArrayList<ParisImp> betList, boolean isWinner) {
		
		Gson gson = new Gson();
		
		for (ParisImp paris : betList) {
			Result result = new Result(isWinner);
			result.amount = getAmount(paris);
			result.winnerTeam = this.winner == Team.TeamA ? match.teamA : match.teamB;
			result.loserTeam = this.winner == Team.TeamA ? match.teamB : match.teamA;
			result.matchId = match.id;

			try {		
				DataOutputStream out = new DataOutputStream(paris.socket.getOutputStream());
				out.writeUTF(gson.toJson(result));
				System.out.println("Envoi des résultats de paris");
			} catch (Exception ex) {
				System.out.println("Oupsss!!! Unable to send bet result to the client");
				System.out.println(String.format("Port: %d", paris.socket.getPort()));
				ex.printStackTrace();
			}

		}
	}

	private double getAmount(ParisImp paris) {
		if (isWinner(paris)) {
			double amountToSplit = (totalAmount * 0.75);
			double ratio = paris.amount / winnerTotalBets;
			return ratio * amountToSplit;
		}
		return 0.0;
	}

	private boolean isWinner(ParisImp paris) {
		Team bettorTeam = getBettorTeam(paris);
		return bettorTeam.equals(winner);
	}

	private Team getBettorTeam(ParisImp paris) {
		if (paris.team.compareTo(match.teamA) == 0)
			return Team.TeamA;
		return Team.TeamB;
	}

	private Team getMatchWinnerTeam() {
		if (match.scoreA > match.scoreB)
			return Team.TeamA;
		return Team.TeamB;
	}

}
