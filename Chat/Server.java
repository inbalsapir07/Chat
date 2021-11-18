import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * The class Server represents a chat server.
 * Question 1, maman 16.
 * 
 * @author (Inbal Sapir)
 * @version (January 25, 2021)
 */
public class Server
{
	// variables
	private static ArrayList <ServerThread> clients= new ArrayList <ServerThread> (); // a list of all the clients handlers
	private static HashMap <Integer, String> participants= new HashMap <Integer, String> (); // a list of names and serial numbers of all the participants in the chat
	// method
	/**
	 * The main method of the program.
	 * Creates threads that handle clients that connected to the chat.
	 * Gives every client a serial number.
	 */
	public static void main(String[] args) 
	{
		ServerSocket serverSocket= null;
		boolean flag= true;
		try 
		{
			serverSocket= new ServerSocket (7777);
		}
		catch (IOException e)
		{
			System.err.println ("couldn't listen to port 7777");
			System.exit(1);
		}
		System.out.println ("server's ready");
		Socket socket= null;
		ServerThread serverThread= null;
		int serial= 0; // every client gets a different serial number
		while (flag)
		{
			try 
			{
				socket= serverSocket.accept();
				serverThread= new ServerThread (socket, clients, participants, serial);
				clients.add(serverThread);
				serverThread.start();
				serial++;
			}
			catch (IOException e)
			{
				System.err.println ("accept failed");
				System.exit(1);
			}
		}
		try
		{
			serverSocket.close();
			socket.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}