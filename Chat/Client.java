import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
/**
 * The class Client represents a client that connected to the chat server.
 * Question 1, maman 16.
 * 
 * @author (Inbal Sapir)
 * @version (January 25, 2021)
 */
public class Client extends WindowAdapter
{
	// variables
	private static Socket socket= null;
	private static PrintWriter out= null; // output stream
	private static ObjectInputStream inObject= null; // input stream for objects
	private static BufferedReader in= null; // input stream for strings
	private static HashMap <Integer, String> participants; // list of the serial numbers and names of all participants in the chat
	private static String message; // assistant variable that saves string inputs from server
	private static String host; // host name
	private static boolean flag= true; // true if this client is connected to server; false otherwise
	// method
	/**
	 * The main method of the program.
	 * Creates the panel of the chat and reads updated participants list
	 * and messages from server.
	 */
	public static void main (String[] args) 
	{
		host=JOptionPane.showInputDialog(null, "Please enter host name:");
		if (host==null || host.equals(""))
			host= "localhost";
		try 
		{
			socket= new Socket (host, 7777);
			out= new PrintWriter (socket.getOutputStream(), true);
			inObject= new ObjectInputStream (socket.getInputStream());
			in= new BufferedReader (new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException e)
		{
			System.out.println("couldn't get I/O for the connection to: "+host);
			System.exit(1);
		}
		System.out.println ("after connections");
		JFrame frame= new JFrame ("Chat");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(500,500);
		ChatPanel chatPanel= new ChatPanel (out);
		frame.add(chatPanel);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter ()
		{
			/**
			 * Handles the event which was invoked by user closing the frame window.
			 * Writes to the server that client exited the program,
			 * closes open streams, disposes frame and ends program. 
			 * @override windowClosing in class WindowAdapter
			 * @param e the event
			 */
			public void windowClosing(WindowEvent e)
			{
				flag=false;
				chatPanel.setChatArea("You left the chat");
				out.println("4");
			}
		});
		while (flag) // while client is connected to server
		{ 
			try 
			{
				participants= (HashMap<Integer, String>) inObject.readObject(); // if participants list has changed, gets updated list; otherwise gets null
				message= in.readLine(); // gets a message to display; if there is no message to display, gets an empty message
				if (participants!=null)
					chatPanel.setParticipantsList(participants.values());
				if (!message.equals(""))
					chatPanel.setChatArea(message);				
			}
			catch (IOException e) 
			{
				System.out.println ("couldn't read from connection");
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			inObject.close();
			in.close();
			out.close();
			socket.close();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		frame.dispose();
	}
}