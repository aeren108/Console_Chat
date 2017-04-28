package chat.serverside;
/** @author Ahmet E. COLAK */
import java.io.IOException;

public class Server {
	private ClientHandler ch;
	
	public Server(){
		try {
			ch = new ClientHandler();
			ch.start();
		} catch (IOException e) {
			System.err.println("\nOops! Something went wrong.");
		}
	}
	
	public static void main(String[] args){
		new Server();
	}
}
