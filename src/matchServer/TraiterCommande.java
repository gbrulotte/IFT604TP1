package matchServer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;

import commands.*;


public class TraiterCommande implements Runnable{

    protected DatagramPacket receivePacket = null;

    public TraiterCommande(DatagramPacket receivePacket) {
        this.receivePacket = receivePacket;
    }

    public void run() {
    	String input = new String(receivePacket.getData());
        String[] typeCommand = input.split("\\~");
        try {
        	switch(typeCommand[0])
        	{
        	case "ListerMatch":
					ListeDesMatchs.queue.put(new ListerMatchCommmand(receivePacket));
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