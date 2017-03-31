import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI implements ActionListener {
	private JButton button[];
	private static final int LENGTH = 50;

	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {
		// Make a frame with a grid content pane.
		JFrame frame = new JFrame("This is great.");
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new GridLayout(0, 10));

		// Setup all buttons with a listener and random background color.
		button = new JButton[LENGTH];
		for (int i = 0; i < LENGTH; i++) {
			button[i] = new JButton("Butt " + i);
			button[i].addActionListener(this);
			contentPane.add(button[i]);
			button[i].setBackground(new Color(
					(int) (Math.random() * 255),
					(int) (Math.random() * 255),
					(int) (Math.random() * 255)
				));
		}

		// Set the window size and make it visible.
		frame.setSize(LENGTH * 100, (int) Math.ceil(LENGTH / 5) * 100);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    frame.setUndecorated(true);
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton)e.getSource();

		// Print the text and set the button color.
		System.out.println(button.getText());
		button.setBackground(new Color(
				(int) (Math.random() * 255),
				(int) (Math.random() * 255),
				(int) (Math.random() * 255)
			));
	}
}
