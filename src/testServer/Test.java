package testServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Test {
	
	public static void main(String args[]) throws Exception    
	{       
		DatagramSocket  socket = new DatagramSocket(1234);
		DatagramPacket  request;
		 
		while(true)
		{
		      String data = null;
		                 
		       try {
		        byte[] buffer = new byte[1024];
		        request         = new DatagramPacket(buffer, buffer.length);
		        socket.receive(request);
		        data = new String(request.getData());
		        data = data.trim();
		        System.out.println(data);
		                //do your processing with request data
		                //Sending response 
		                String response = "Test Reply from UDP server!";
		                DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), request.getAddress(), request.getPort());
		        socket.send(reply);
		    }
		    catch(Exception err) {
		        err.printStackTrace();
		    } 
		}   
	}
}
