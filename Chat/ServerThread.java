import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * The class ServerThread handles a client that connected to the chat.
 * 
 * @author (Inbal Sapir)
 * @version (January 25, 2021)
 */
public class ServerThread extends Thread
{
	// variables
	private Socket socket= null;
	private ObjectOutputStream outObject= null; // output stream for objects
	private PrintWriter out= null; // output stream for strings
	private BufferedReader in= null; // input stream
	private String input; // assistant variable that saves inputs from client
	private ArrayList <ServerThread> clients= null; // list of all clients connected to server
	private HashMap <Integer, String> participants= null; // list of the serial numbers and names of all participants in the chat
	private int serialNumber; // the serial number of the client that this ServerThread handles
	private String name; // the name of the client that this ServerThread handles
	private boolean join; // true if the client that this ServerThread handles joined the chat; false otherwise
	private boolean flag; // true if the client that this ServerThread handles is connected to server; false otherwise
	// constructor
	/**
	 * Constructs a new ServerThread to handle a client 
	 * using the socket and the serial number of the client received from server.
	 * Saves an ArrayList of ServerThreads of the other clients that connected to the server
	 * and a HashMap of names and serial numbers of the other clients that joined the chat.
	 * @param socket the socket to connect to the client that this ServerThread handles
	 * @param clients an ArrayList of ServerThreads of the other clients that are connected to the server
	 * @param participants a HashMap of names and serial numbers of the other clients that joined the chat
	 * @param serial the serial number of the client that this ServerThread handles
	 */
	public ServerThread (Socket socket, ArrayList <ServerThread> clients, HashMap <Integer, String> participants, int serial)
	{
		this.socket= socket;
		try 
		{
			outObject= new ObjectOutputStream (socket.getOutputStream());
			outObject.flush();
			out= new PrintWriter (socket.getOutputStream(), true);
			in= new BufferedReader (new InputStreamReader(socket.getInputStream()));
		} 
		catch (IOException e) 
		{
			System.out.println("couldn't open I/O connection");
		}
		this.clients= clients;
		this.participants= participants;
		serialNumber= serial;
		join= false;
		flag= true;
	}
	// method
	/**
	 * Handles actions made by client.
	 * if client joined chat, saves client's name, adds client's name and serial number
	 * to the HashMap of the other clients, updates participants list and sends it
	 * to the other clients in the chat, sends joining message to the other clients in the chat.
	 * if client sent a message, sends the message to the other clients in the chat.
	 * if client left chat, removes client's name and serial number from the HashMap
	 * of the other clients, updates participants list and sends it
	 * to the other clients in the chat, sends leaving message to the other clients in the chat.
	 * if client exited program, closes open streams and ends program.
	 * @override run in class Thread
	 */
	public void run ()
	{
		try
		{
			while (flag) // while client is connected to server
			{
				input= in.readLine();
				if (input.charAt(0)=='1') // if the participant joined chat
				{
					join= true;
					name= input.substring(1);
					participants.put (serialNumber, name);
					for (ServerThread sereverThread: clients)
					{
						if (sereverThread.join)
						{
							sereverThread.outObject.reset();
							sereverThread.outObject.writeObject(participants);
							sereverThread.outObject.flush();
							if (sereverThread!=this)
								sereverThread.out.println(name+" joined the chat");
							else
								sereverThread.out.println("");
						}
					}
				}
				if (input.charAt(0)=='2') // if the participant sent a message
				{
					for (ServerThread sereverThread: clients)
					{	
						if (sereverThread!=this && sereverThread.join)
						{
							sereverThread.outObject.reset();
							sereverThread.outObject.writeObject(null);
							sereverThread.outObject.flush();
							sereverThread.out.println(input.substring(1));
						}
					}
				}
				if (input.charAt(0)=='3') // if the participant left chat
				{
					join= false;
					participants.remove(serialNumber);
					for (ServerThread sereverThread: clients)
					{
						if (sereverThread.join)
						{
							sereverThread.outObject.reset();
							sereverThread.outObject.writeObject(participants);
							sereverThread.outObject.flush();
							sereverThread.out.println(name+" left the chat");
						}
					}
				}
				if (input.charAt(0)=='4') // if the participant exited program
				{
					join= false;
					flag= false;
					participants.remove(serialNumber);
					for (ServerThread sereverThread: clients)
					{
						if (sereverThread.join)
						{
							sereverThread.outObject.reset();
							sereverThread.outObject.writeObject(participants);
							sereverThread.outObject.flush();
							sereverThread.out.println(name+" left the chat");
						}
					}
					outObject.reset();
					outObject.writeObject(participants);
					outObject.flush();
					out.println("");
				}
			}
			socket.close();
			outObject.close();
			out.close();
			in.close();
		}
		catch (IOException e)
		{
			System.out.println ("couldn't read from connection");
		}
	}
}