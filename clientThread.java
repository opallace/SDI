import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

class clientThread extends Thread {

	private DataInputStream is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;

	public clientThread(Socket clientSocket, clientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}

	public void run() {
    	int maxClientsCount = this.maxClientsCount;
    	clientThread[] threads = this.threads;

		try {
		
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			os.println("Enter your name.");
			String name = is.readLine().trim();
			os.println("Hello " + name+ " to our chat room.\nTo leave enter /quit in a new line");
			
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] != null && threads[i] != this) {
				threads[i].os.println("*** A new user " + name
					+ " entered the chat room !!! ***");
				}
			}
			
			while (true) {
				String line = is.readLine();
				
				if (line.startsWith("/quit")) {
					break;
				}
				
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null) {
						threads[i].os.println("<" + name + ">; " + line);
					}
				}
			}

			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] != null && threads[i] != this) {
					threads[i].os.println("*** The user " + name+ " is leaving the chat room !!! ***");
				}
			}

			os.println("*** Bye " + name + " ***");

			
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] == this) {
					threads[i] = null;
				}
			}

			
			is.close();
			os.close();
			clientSocket.close();

		} catch (IOException e) {}
	}
}
