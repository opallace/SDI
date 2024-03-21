import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;


public class Server {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket       = null;

    private static String login_session    = "admin";
    private static String password_session = "12345";

    public static void main(String args[]) {
        
        try {
            serverSocket = new ServerSocket(2222);
        } catch (IOException e) {
            System.out.println(e);
        }

        Vector<String> authorized_hosts = new Vector<String>();

        while(true) {
            try {
                clientSocket       = serverSocket.accept();
                PrintStream os     = new PrintStream(clientSocket.getOutputStream());
                DataInputStream is = new DataInputStream(clientSocket.getInputStream());
                
                String login    = is.readLine();
                String password = is.readLine();

                if(login.startsWith(login_session) && password.startsWith(password_session)){
                    os.println("[+] Login aprovado.");
                    System.out.println("[+] Cliente " + clientSocket.getInetAddress().getHostAddress() + " autorizado.");
                    authorized_hosts.add(clientSocket.getInetAddress().getHostAddress());
                }else {
                    os.println("[+] Login não aprovado.");
                    os.println("[+] Desconectando...");
                }

                os.close();
                is.close();
                clientSocket.close();
                
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}