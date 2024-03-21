//Example 25

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client{

    private static Socket clientSocket      = null;
    private static MulticastSocket socket   = null;
    private static PrintStream os           = null;
    private static DataInputStream is       = null;
    private static BufferedReader inputLine = null;
    private static boolean tcp_closed           = false;

    public static void main(String[] args) {

        int portNumber = 2222;
        String host    = "localhost";
        
        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
       
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
       
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "+ host);
        }

        if (clientSocket != null && os != null && is != null) {
            try {
                new Thread(tcp_thread).start();
                
                Scanner ler = new Scanner(System.in);
                String msg;

                while (!tcp_closed) {
                    msg = ler.nextLine();
                    os.println(msg);
                }
               
                os.close();
                is.close();
                clientSocket.close();
           
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }

		DatagramPacket inPacket = null;
		byte[] inBuf = new byte[256];
		
		try {
		
			socket = new MulticastSocket(8888);    
			InetAddress address = InetAddress.getByName("224.0.0.2");

			socket.joinGroup(address);
            
            new Thread(udp_thread).start();
	
			while (true) {
				inPacket = new DatagramPacket(inBuf, inBuf.length);
				socket.receive(inPacket);
				String msg = new String(inBuf, 0, inPacket.getLength());
				System.out.println("From " + inPacket.getAddress() + " Msg : " + msg);
			}
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
    }

    private static Runnable tcp_thread = new Runnable() {
        public void run() {
            String responseLine;
        
            try {
            
                while ((responseLine = is.readLine()) != null) {
                    System.out.println(responseLine);
                
                    if (responseLine.indexOf("*** Bye") != -1) {
                        break;
                    }
                }
                
                tcp_closed = true;
            
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    };

    private static Runnable udp_thread = new Runnable() {
        public void run() {
            Scanner ler = new Scanner(System.in);
            String msg;
            
            try {
            
                while (true) {
                    msg = ler.nextLine();
                    InetAddress address = InetAddress.getByName("224.0.0.2");

                    DatagramPacket outPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, address, 8888);

                    socket.send(outPacket);
                }
                
            
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    };

}
