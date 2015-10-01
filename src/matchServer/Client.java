package matchServer;

import java.net.InetAddress;

public class Client {
	public InetAddress address;
	public int port;
	
	public Client(InetAddress address, int port){
		this.address = address;
		this.port = port;
	}
}
