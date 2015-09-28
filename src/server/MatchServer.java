package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchServer implements Runnable {
//threadpool
	protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool = null;
    
    public MatchServer(int serverPort, int nbThreads){
    	this.serverPort = serverPort;
    	this.threadPool = Executors.newFixedThreadPool(nbThreads);
    }
    
    public void run(){
    	synchronized(this){
    		this.runningThread = Thread.currentThread();
    	}
    	openServerSocket();
    	while(!isStopped()){
    		Socket clientSocket = null;
    		try{
    			clientSocket = this.serverSocket.accept();
    		}
    		catch(IOException e){;
    			System.out.println("Unabled to accept client connection : " + e.toString());
    		}
    		this.threadPool.execute(new WorkerRunnable(clientSocket, "Thread pooled Server"));
    	}
    	this.threadPool.shutdown();
    	System.out.println("Server stopped");
    }
    
	private void openServerSocket() {
		try{
			this.serverSocket = new ServerSocket(serverPort);
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
		try{
			this.serverSocket.close();
		}
		catch(IOException e){
			System.out.println("Problem while closing server : " + e.toString());
		}
	}
}
