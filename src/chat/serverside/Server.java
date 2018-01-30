/** @author Ahmet E. COLAK */
package chat.serverside;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Server implements Runnable{
	private ServerSocket server = null;
	private Socket socket = null;
	
	private boolean running;
	private final int port = 2002; //Default port
	
	private static ArrayList<ClientThread> clientList;
	protected static Set<String> codenames;
	
	private static int clientNum;
	
	//ServerSocket with the default port
	public Server() throws IOException{
		server = new ServerSocket(port);
		clientList = new ArrayList<>();
		codenames = new HashSet<String>();
	}
	
	//ServerSocket with a custom port
	public Server(int port) throws IOException{
		server = new ServerSocket(port);
		clientList = new ArrayList<>();
		codenames = new HashSet<String>();
	}
	
	@Override
	public void run() {
		try {
			clientNum = 50;
			
			while (running){
				socket = server.accept(); //Waits for a connection 
				
				String codename = "#"+ clientNum;
				clientList.add(new ClientThread(socket, codename)); //Adds the client to arraylist
				
				codenames.add(codename);
				
				for (ClientThread ct : clientList){
					if (ct.getSocket().isClosed()){
						clientList.remove(ct);
					}
				}
				
				for (ClientThread ct : clientList) //Starts the every client thread
					ct.start();
				
				clientNum++;
			}
			
		} catch (IOException e) {
			System.err.println("\nOops! Something went wrong.");
		}
	}
	
	public static ArrayList<ClientThread> getClients(){
		return clientList;
	}
	
	public static void whisper(String from, String to, String msg) throws IOException{
		for (ClientThread ct : clientList){
			if (to.equals(ct.getName())){
				PrintWriter out = new PrintWriter(ct.getSocket().getOutputStream(), true);
				out.println("<" + from + ">" + " whispered: " + msg);
			}
		}
	}
	
	public void start(){
		if (running)
			return;
		
		running = true;
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop() throws IOException{
		if (!running)
			return;
		
		running = false;
		socket.close();
	}
	
	//Sends message to all clients
	public void broadcast(String message) throws IOException{
		for (ClientThread ct : clientList)
			ct.sendMessage(message);
	}
	
	public static void main(String[] args) {
		try {
			Server ch = new Server();
			ch.start();
		} catch (IOException e) {
			System.err.println("Server couldn't be started");
		}
	}
}
