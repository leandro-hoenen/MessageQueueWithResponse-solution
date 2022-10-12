package ch.fhnw.digi.demo;

import javax.swing.JFrame;
import javax.swing.JTextArea;


public class SimpleUi extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextArea lbl;


	void init() {
		setSize(300, 120);
		setTitle("ActiveMQ Broker");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		lbl = new JTextArea();
		lbl.setEditable(false);
		getContentPane().add(lbl);
		setVisible(true);
	}

	public void setMessage(String string) {
		lbl.setText("Broker running ...\n\n" + string + "\n\nClose window to terminate");
	}

}
