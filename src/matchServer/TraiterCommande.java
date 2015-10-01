package matchServer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import commands.*;


public class TraiterCommande implements Runnable{

    protected DatagramSocket serverSocket = null;
    protected DatagramPacket receivePacket = null;
    protected String input = "";

    public TraiterCommande(DatagramSocket serverSocket, DatagramPacket receivePacket, String input) {
        this.serverSocket = serverSocket;
        this.receivePacket = receivePacket;
        this.input = input;
    }

    public void run() {
        String[] typeCommand = input.split("\\~");
        try {
        	switch(typeCommand[0])
        	{
        	case "ListerMatch":
					ListeDesMatchs.queue.put(new ListerMatchCommmand(serverSocket, receivePacket));
        		break;
        		default:
        			System.out.println("Commande inconnue");
        			break;
        	}
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}