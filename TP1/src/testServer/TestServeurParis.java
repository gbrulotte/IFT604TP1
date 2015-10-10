package testServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TestServeurParis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String sentence;
			String modifiedSentence;
			BufferedReader inFromUser = new BufferedReader(
					new InputStreamReader(System.in));
			Socket clientSocket = new Socket(InetAddress.getByName("localhost"), 8081);
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));

			sentence = inFromUser.readLine();
			outToServer.writeBytes(sentence);
			clientSocket.close();
		} catch (Exception ex) {
			System.out.println(ex);
			
		}
		
	}
}
