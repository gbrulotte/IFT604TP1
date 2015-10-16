package commands;

import java.util.UUID;

import matchServer.ListeDesMatchs;

public class EnleverMatchCommand implements ICommand{

	public UUID id;
	
	public EnleverMatchCommand(UUID id){
		this.id = id;
	}
	@Override
	public void execute() {
		ListeDesMatchs.matches.remove(id);
	}
}
