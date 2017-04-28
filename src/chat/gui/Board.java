package chat.gui;

import java.awt.*;
import java.awt.event.*;

import java.io.IOException;

import javax.swing.*;
import chat.clientside.*;

public class Board extends JPanel{
	private Client cl;
	private JFrame frame;

	private JTextArea text;
	private JTextField msg;
	private JScrollPane scroll;

	private JButton send;
	private JButton cond;
	private JButton disc;
	private JButton clear;

	private KeyManager key;

	private int width, height;

	private static final long serialVersionUID = 1L;

	public Board(Client cl, int width, int height){
		this.cl = cl;
		this.width = width;
		this.height = height;

		init(this.width, this.height);
		buttonEvents();
	}

	//Initisalization function.
	public void init(int width, int height){
		frame = new JFrame("Chat");

		text = new JTextArea();
		msg = new JTextField();

		send = new JButton();
		cond = new JButton();
		disc = new JButton();
		clear = new JButton();
		
		key = new KeyManager(this);

		frame.setSize(new Dimension(width, height));
		frame.setLocation(new Point(0, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		setLayout(null);

		text.setSize(new Dimension(500, 460));
		text.setLocation(new Point(20, 15));
		text.setEditable(false);

		msg.setSize(new Dimension(400, 75));
		msg.setLocation(new Point(20, 480));

		send.setText("Send");
		send.setSize(new Dimension(94, 75));
		send.setLocation(new Point(425, 480));

		cond.setText("Connect");
		cond.setSize(new Dimension(120, 50));
		cond.setLocation(new Point(550, 15));
		
		disc.setText("Disconnect");
		disc.setSize(new Dimension(120, 50));
		disc.setLocation(new Point(550, 75));
		
		clear.setText("Clear Chat");
		clear.setSize(new Dimension(120, 50));
		clear.setLocation(new Point(550, 135));

		scroll = new JScrollPane(text);
		scroll.setBounds(20, 15, 500, 460);
		scroll.setAutoscrolls(true);

		add(msg);
		add(cond);
		add(send);
		add(disc);
		add(clear);
		add(scroll);

		frame.add(this);//Adding to frame to this reference

		frame.setVisible(true);//Setting layout to null to put the object anywhere in window


		revalidate();
		frame.revalidate();
		frame.addKeyListener(key);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);

				try {
					cl.stop();
					System.exit(0);
				} catch (IOException e1) {
					System.err.println("Messages couldn't be saved");
				}
			}
		});
		//frame.setFocusable(true);
	}

	public JButton getConnectButton(){return cond;}
	public JButton getSendButton(){return send;}

	public void buttonEvents(){
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent a) {
				cl.sendEvent();
			}
		});

		cond.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cl.defaultConnect();
			}
		});
		
		disc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cl.disconnect();
				} catch (IOException e1) {
					System.err.println("Couldn't disconnect.");
				}
				
			}
		});
		
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				text.setText("");
			}
		});
	}

	public void sendEvent(){
		cl.sendEvent();
	}

	public void addMessage(String message){
		text.append(message + "\n");
		text.setCaretPosition(text.getDocument().getLength());
	}

	public String getMessage(){return msg.getText();}

	public String getDialog(){return text.getText();}

	public void setDialog(String txt){text.setText(txt);}

	public void clearBox(){msg.setText("");}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		////////////CLEAR\\\\\\\\\\\\\


		/////////////DRAW\\\\\\\\\\\\\\



		/////////////*END*\\\\\\\\\\\\\\

		g.dispose();
	}
}
