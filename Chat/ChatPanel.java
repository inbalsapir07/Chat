import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.*;
/**
 * The class ChatPanel represents a chat panel.
 * 
 * @author (Inbal Sapir)
 * @version (January 25, 2021)
 */
public class ChatPanel extends JPanel
{
	// variables
	private PrintWriter out; // output stream
	private String output; // assistant variable for outputs to server
	private String name; // the name of the client
	private AListener actionListener = new AListener(); // listener for action events
	private JTextField typingField; // writing a message field
	private JButton sendButton; // send message button
	private JPanel north; // the north panel, to send a message
	private DefaultListModel <String> list; // saves the participants list
	private JList<String> participantsList; // the participants list to display
	private JScrollPane participantsScroll; // participants list scroll pane
	private JTextArea chatArea; // chat area
	private JScrollPane chatScroll; // chat area scroll pane
	private JPanel center; // the center panel, displays participants list and chat messages
	private JButton joinChatButton; // join chat button
	private JButton leaveChatButton; // leave chat button
	private JPanel south; // the south panel, to join or leave the chat
	// constructor
	/**
	 * Constructs a new chat panel.
	 * The chat panel displays the current participants list of the chat,
	 * displays messages from other users, and allows user to write messages,
	 * join and leave the chat.  
	 * @param out the output stream that sends messages to server, by the user's actions. 
	 */
	public ChatPanel (PrintWriter out)
	{
		this.out= out;
		// north panel
		typingField= new JTextField (35);
		typingField.addActionListener(actionListener);
		typingField.setEnabled(false);
		sendButton= new JButton ("Send");
		sendButton.addActionListener(actionListener);
		sendButton.setEnabled(false);
		north= new JPanel();
		north.add(typingField);
		north.add(sendButton);
		// center panel
		list= new DefaultListModel <String> ();
		participantsList= new JList <String> (list);
		participantsScroll= new JScrollPane (participantsList);
		chatArea= new JTextArea ();
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
		chatScroll= new JScrollPane (chatArea);
		center= new JPanel ();
		center.setLayout(new BorderLayout());
		center.add (participantsScroll, BorderLayout.WEST);
		center.add (chatScroll, BorderLayout.CENTER);
		// south panel
		joinChatButton= new JButton ("Join chat");
		joinChatButton.addActionListener(actionListener);
		joinChatButton.setEnabled(true);
		leaveChatButton= new JButton ("Leave chat");
		leaveChatButton.addActionListener(actionListener);
		leaveChatButton.setEnabled(false);
		south= new JPanel ();
		south.add(joinChatButton);
		south.add(leaveChatButton);
		// organizing panels in the chat panel
		setLayout (new BorderLayout());
		add (north, BorderLayout.NORTH);
		add (center, BorderLayout.CENTER);
		add (south, BorderLayout.SOUTH);
	}
	// methods
	/**
	 * The class AListener handles relevant action events.
	 */
	private class AListener implements ActionListener
	{
		/**
		 * Handles the event which was invoked by user clicking on a button
		 * or using the enter key for sending a message.
	     * If user joined chat, gets a name from user, enables and disables relevant components,
	     * and sends the user's name to server.
	     * If user sent a message, sends the user's message to server.
	     * If user left chat, writes to server about user leaving the chat
	     * and enables and disables relevant components.
		 * @override actionPerformed in interface ActionListener
		 * @param e the event
		 */
		public void actionPerformed (ActionEvent e)
		{
			if (e.getSource() instanceof JButton) // if user clicked on one of the buttons
			{
				if (((JButton)e.getSource()).getText().equals("Join chat")) // if user clicked the join chat button
				{
					name= ""+JOptionPane.showInputDialog(null, "Please enter your name:");
					while (name==null || name.length()<=0)
						name= ""+JOptionPane.showInputDialog(null, "Please enter your name:");
					typingField.setEnabled(true);
					sendButton.setEnabled(true);
					participantsList.setEnabled(true);
					chatArea.setEnabled(true);
					participantsScroll.setEnabled(true);
					chatScroll.setEnabled(true);
					joinChatButton.setEnabled(false);
					leaveChatButton.setEnabled(true);
					setChatArea("You joined the chat");
					output=""+1+name;
					out.println(output);
				}
				if (((JButton)e.getSource()).getText().equals("Send")) // if user clicked the send message button
				{
					setChatArea ("You: "+typingField.getText());
					output=""+2+name+": "+typingField.getText();
					typingField.setText("");
					out.println(output);
				}
				if (((JButton)e.getSource()).getText().equals("Leave chat")) // if user clicked the leave chat button
				{
					setChatArea ("You left the chat");
					output=""+3;
					out.println(output);
					typingField.setText("");
					typingField.setEnabled(false);
					sendButton.setEnabled(false);
					participantsList.setEnabled(false);
					chatArea.setEnabled(false);
					participantsScroll.setEnabled(false);
					chatScroll.setEnabled(false);
					joinChatButton.setEnabled(true);
					leaveChatButton.setEnabled(false);
				}
			}
			if (e.getSource() instanceof JTextField) // if user sent a message using the enter key
			{
				setChatArea ("You: "+typingField.getText());
				output=""+2+name+": "+typingField.getText();
				typingField.setText("");
				out.println(output);
			}			
		}
	}
	/**
	 * Adds a string to the chat area, using a reference string.
	 * @param message the reference string
	 */
	public void setChatArea (String message)
	{
		chatArea.append(message+"\n");
		revalidate();
	}
	/**
	 * Updates the participants list, using a reference collection 
	 * of the names of the participants.
	 * @param participants the reference collection
	 */
	public void setParticipantsList (Collection<String> participants)
	{
		Iterator <String> iterator= participants.iterator();
		list.clear();
		while (iterator.hasNext())
			list.addElement(iterator.next());
		revalidate();
	}
}