/** @author Ahmet E. COLAK */
package chat.clientside;

import java.io.*;
import java.net.*;
import chat.file.*;
import chat.gui.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Client{
	private Socket socket;
	private ChatUtilities chat;
	
	private Editor editor;

	private BufferedReader msg;

	private Board panel;
	private JFileChooser dir;
	private String path;
	
	private String host;
	private int port;
	
	private boolean connected;
	
	public Client() {
		init();
		
		String message;
		while (connected){
			try {
				message = msg.readLine();
				
				if (message.startsWith("/whisper-")){
					String[] words = message.split("-", 0);
					String first = words[0];
					String second = words[1];
					String third = words[2];
					chat.sendCustomMessage(first + "-" + chat.getName() + "-" + second + "-" + third);
					panel.addMessage("You > whispered " + second + ": " + third);
				}
				
				else if (!message.isEmpty() && !message.equals(null)){
					chat.sendMessage(message);
					panel.addMessage("<"+chat.getName()+"> :" + message);
				}
				
			} catch (IOException e) {
				System.err.println("Wait a few seconds..");
			}
		}
	}
	
	public void sendEvent(){
		try {
			String mesg = panel.getMessage();
			
			if (mesg.startsWith("/whisper-")){
				String[] words = mesg.split("-", 0);
				String first = words[0];
				String second = words[1];
				String third = words[2];
				chat.sendCustomMessage(first + "-" + chat.getName() + "-" + second + "-" + third);
				panel.addMessage("You > whispered " + second + ": " + third);
				panel.clearBox();
			}
			else if (!mesg.isEmpty() && !mesg.equals(null)){
				chat.sendMessage(mesg);
				panel.addMessage("<"+chat.getName()+"> :" + mesg);
				panel.clearBox();
			}
		} catch (IOException e) {
			System.err.println("Message couldn't be sent");
		}
	}
	
	public void defaultConnect(){
		try {
			connected = true;
			
			host = "localhost";
			port = 2002;
			
			socket = new Socket(host, port);
			
			chat = new ChatUtilities(socket);
			chat.start();
		} catch (PortUnreachableException e) {
			System.err.println("\nPort is unreachable, please check the server.");
		} catch (IOException e) {
			System.err.println("\nOops! Something went wrong.");
		}
	}
	
	public void disconnect() throws IOException{
		connected = false;
		
		panel.getSendButton().setEnabled(false);
		
		chat.stop();
		socket.close();
		socket = null;
	}
	
	public void customConnect(){
		try {
			connected = true;
			
			host = JOptionPane.showInputDialog("Host's/Server's IP ");
			port = Integer.parseInt(JOptionPane.showInputDialog("Host's/Server's port "));
			
			socket = new Socket(host, port);
			
			chat = new ChatUtilities(socket);
			chat.start();
		} catch (PortUnreachableException e) {
			System.err.println("\nPort is unreachable, please check the server.");
		} catch (IOException e) {
			System.err.println("\nOops! Something went wrong.");
		}
	}
	
	//Initializes the GUI
	public void init(){
		panel = new Board(this, 800, 600);
		editor = new Editor();
		dir = new JFileChooser();
		dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dir.setDialogTitle("Select directory for saving messages");
		dir.showOpenDialog(dir);
		path = dir.getSelectedFile().getAbsolutePath();
	
		editor.createFile(path + "/messages.txt");
		panel.setDialog(editor.readFile(path + "/messages.txt", true));
		
		editor.createFile(path + "/name.txt");
		
		if (editor.readFile(path + "/name.txt", false).isEmpty() || 
			editor.readFile(path + "/name.txt", false).equals(null) || 
			editor.readFile(path + "/name.txt", false) == ""){ //Checking all possibilities
			System.out.println("Not registered.");
		}
		else {
			defaultConnect();
		}
		
		panel.revalidate();
	}
	
	public void stop() throws IOException{chat.stop();}
	
	public static void main(String[] args){
		new Client();
	}
	
	//This class is for receiving and sending messages 
	class ChatUtilities implements Runnable{
		private Socket socket;
		
		private PrintWriter sender;
		private BufferedReader reader;
		private BufferedReader input;
		
		private String name;
		private Thread thread;
		
		private boolean running;
		
		public ChatUtilities(Socket socket) throws IOException {
			this.socket = socket;
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.sender = new PrintWriter(socket.getOutputStream(), true);
			this.input = new BufferedReader(new InputStreamReader(System.in));
			
			if (editor.readFile(path + "/name.txt", false).isEmpty() || 
				editor.readFile(path + "/name.txt", false).equals(null) || 
				editor.readFile(path + "/name.txt", false) == "") {
				
				name = JOptionPane.showInputDialog("Your username? ");
				editor.writeFile(path + "/name.txt", name);
			}
			else {
				this.name = editor.readFile(path + "/name.txt", false);
			}
		}
		
		public void setName(String name){this.name = name;}
		
		@Override
		public void run() {
			try {
				System.out.println("Connected to " + socket.getInetAddress().getHostName());
				sender.println("Server: @" + name + " has joined");
				
				String message;
				while (running){
					message = reader.readLine();// Reads the message
					
					if (connected)
						panel.getConnectButton().setEnabled(false);
					else
						panel.getConnectButton().setEnabled(true);
					
					if (!message.isEmpty() && !message.equals(null)){
						if (!message.startsWith("<"+name+">") && !message.startsWith(name)) { // For not to see our own message
							System.out.println(message);
							panel.addMessage(message);
						}
					}	
				}
			} catch (IOException e) {
				System.err.println("\nOops! Something went wrong.");
			}
		}
		
		public void sendMessage(String message) throws IOException{
			sender.println("<"+name+"> :" + message);
		}
		
		public void sendCustomMessage(String message) throws IOException{
			sender.println(message);
		}
		
		public String getName(){return name;}
		
		public void start(){
			if (running)
				return;
			
			panel.getSendButton().setEnabled(true);
			
			running = true;
			
			thread = new Thread(this);
			thread.start();
		}

		public void stop() throws IOException{
			if(!running)
				return;
			
			editor.writeFile(path + "/messages.txt", panel.getDialog());
			
			running = false;
			
			sendCustomMessage(name + " has left");
			
			reader.close();
			sender.close();
			input.close();
			socket.close();
		}	
	}
}


