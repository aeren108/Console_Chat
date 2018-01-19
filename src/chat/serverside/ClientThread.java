/** @author Ahmet E. COLAK */
package chat.serverside;

import java.io.*;
import java.net.*;

public class ClientThread implements Runnable {
	private Socket socket;
	
	private BufferedReader reader;
	private PrintWriter sender;

	private boolean running;
	private String codename;
	private String name;

	//Defines the streams and socket
	public ClientThread(Socket socket, String codename) throws IOException {
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.sender = new PrintWriter(socket.getOutputStream(), true);
		this.codename = codename;
	}

	@Override
	public void run() {
		try {
			String message;
			while (running){
				message = reader.readLine(); //Reads the message

				if (message.startsWith("Server:")){
					name = message.substring(8, message.length()-11);

					sendMessage("Your codename's: " + codename);
					System.out.println(name);

					for (ClientThread ct : Server.getClients())// Echoes the message to all clients
						ct.sendMessage(message);

				}//Server: xx has joined

				else if (message.contains("/whisper-")){
					System.out.println("Whispered");
					String from;
					String to;
					String msg;

					String[] words = message.split("-", 0);
					from = words[1];
					to = words[2];
					msg = words[3];

					System.out.println("from: "+ from);
					System.out.println("to: " + to);
					System.out.println("message: " + msg);

					for (String word : words)
						System.out.println(word);

					Server.whisper(from, to, msg);
				}

				else {
					for (ClientThread ct : Server.getClients())// Echoes the message to all clients
						ct.sendMessage(message);
				}
			}
		} catch (IOException e) {
			System.err.println("\nOops! Something went wrong");
		}
	}

	public void sendMessage(String message) throws IOException{sender.println(message);}

	public String getCodeName(){return codename;}

	public Socket getSocket(){return socket;}

	public String getName(){return name;}

	public void start(){
		if (running) //If it's already running, we shouldn't start the thread again
			return;

		running = true;

		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop() throws IOException{
		if(!running)
			return;

		running = false;

		reader.close();
		sender.close();
		socket.close();
	}
}
