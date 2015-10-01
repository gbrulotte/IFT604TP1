package testServer;

import java.io.*; 
import java.net.*;

class TestCalice {    
	public static void main(String args[]) throws Exception       
	{          
		DatagramSocket  socket = new DatagramSocket();
		try{
		         String requestData         = "'Hello World' via UDP in JAVA";
		         byte [] m              = requestData.getBytes();
		         InetAddress aHost      = InetAddress.getByName("localhost");
		         int serverPort             = 1234;
		         DatagramPacket request     = new DatagramPacket(m, requestData.length(), aHost, serverPort);
		         socket.send(request);
		         byte [] buffer = new byte[1000];
		         DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		         socket.setSoTimeout(2000);
		         socket.receive(reply);
		         String data = new String(reply.getData());
			        data = data.trim();
			        System.out.println(data);
		}
		catch(SocketTimeoutException e){
		    e.printStackTrace();
		}
		catch(Exception e){
		        e.printStackTrace();
		}finally{
		    socket.close();
		}	      
	} 	
}