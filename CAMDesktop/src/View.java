import java.awt.*;
import javax.swing.*;

import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
	private JButton resetButton, setButton;
	private JLabel deviceLabel;
	private JList<String> deviceList;
	private JTextField counterField, valueField;

	public View() {
		// Make the frame with a border layout.
		JFrame frame = new JFrame("CAM");
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());

		// Create panels.
		JPanel devicePanel = new JPanel();
		devicePanel.setLayout(new BorderLayout());
		JPanel infoPanel = new JPanel();
		contentPane.add("West", devicePanel);
		contentPane.add("East", infoPanel);

		final DefaultListModel<String> fruitsName = new DefaultListModel<>();
		
		fruitsName.addElement("Apple");
		fruitsName.addElement("Grapes");
		fruitsName.addElement("Mango");
		fruitsName.addElement("Peer");
		
		deviceList = new JList<>(fruitsName);
		devicePanel.add("Center", deviceList);
		
		// Setup device label + list.
		deviceLabel = new JLabel("Loading...");
		devicePanel.add("East", deviceLabel);
		deviceLabel.setVisible(false);

		// Set size and make the window visible.
		frame.setSize(600, 800);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Exit the program when the frame closes.
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deviceLabel.setVisible(true);
	}

	@Override
	public void update(Observable model, Object obj) {
		int value = (int) obj;

		counterField.setText(Integer.toString(value));
	}

	/**
	 * Add button listeners to the controller.
	 * 
	 * @param controller
	 */
	public void addListeners(Controller controller) {
		resetButton.addActionListener(controller);
		setButton.addActionListener(controller);
	}

	/**
	 * Check if the passed object is the reset button.
	 * 
	 * @param obj
	 *            button
	 * @return boolean
	 */
	public boolean isResetButton(Object obj) {
		return resetButton.equals(obj);
	}

	/**
	 * Check if the passed object is the set button.
	 * 
	 * @param obj
	 * @return boolean
	 */
	public boolean isSetButton(Object obj) {
		return resetButton.equals(obj);
	}

	/**
	 * Get the value field text.
	 * 
	 * @return String
	 */
	public String getValueField() {
		return valueField.getText();
	}

}
