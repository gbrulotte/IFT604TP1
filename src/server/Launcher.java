package server;

public class Launcher {

	public static void main(String[] args) {
		MatchServer server = new MatchServer(9000, 10);
		new Thread(server).start();

		try {
		    Thread.sleep(20 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
