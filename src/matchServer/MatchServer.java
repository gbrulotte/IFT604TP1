package matchServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchServer implements Runnable {
	protected int          serverPort   = 8080;
    protected DatagramSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool = null;
    
    byte[] receiveData = new byte[1024];             
    byte[] sendData = new byte[1024];
    
    public MatchServer(int serverPort, int nbThreads){
    	this.serverPort = serverPort;
    	this.threadPool = Executors.newFixedThreadPool(nbThreads);
    }
    
    public void run(){
		new Thread(new ListeDesMatchs()).start();
		
    	synchronized(this){
    		this.runningThread = Thread.currentThread();
    	}
    	openServerSocket();
    	while(!isStopped()){
    		DatagramPacket receivePacket = null;
    		try{
    			receivePacket = new DatagramPacket(receiveData, receiveData.length);
    			serverSocket.receive(receivePacket);
    			System.out.println("MatchServer: Receiving command");
    		}
    		catch(IOException e){
    			System.out.println("Unabled to accept client connection : " + e.toString());
    		}
    		this.threadPool.execute(new TraiterCommande(serverSocket, receivePacket, new String(receivePacket.getData())));
    	}
    	this.threadPool.shutdown();
    	System.out.println("Server stopped");
    }
    
	private void openServerSocket() {
		try{
			this.serverSocket = new DatagramSocket(serverPort);
		}
		catch(IOException e){
			System.out.println("Problem while opening server socket on port " + serverPort + " : " + e.toString());
		}
	}

	private synchronized boolean isStopped() {
		return isStopped;
	}
	
	public synchronized void stop(){
		this.isStopped = true;
		this.serverSocket.close();
	}
}
