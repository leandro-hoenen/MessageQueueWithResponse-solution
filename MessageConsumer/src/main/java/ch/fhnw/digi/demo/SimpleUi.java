package ch.fhnw.digi.demo;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.springframework.stereotype.Component;

@Component
public class SimpleUi extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextArea lbl;

	@PostConstruct
	void init() {
		setSize(300, 120);
		setTitle("Message Consumer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		lbl = new JTextArea();
		lbl.setEditable(false);
		getContentPane().add(lbl);
		setVisible(true);
	}

	public void setMessage(String string) {
		lbl.setText("Message consumer running ...\n\n" + string + "\n\nClose window to terminate");
	}

}
