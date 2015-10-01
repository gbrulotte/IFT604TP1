package testServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Test {
	
	public static void main(String[] args){
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), 8080);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			
			oos.writeObject("ListerMatch");
			String message = (String) ois.readObject();
            System.out.println("Message: " + message);
            
            //close resources
            ois.close();
            oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
