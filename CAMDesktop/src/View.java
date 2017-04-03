import java.awt.*;
import javax.swing.*;

import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
	private JPanel sidebar;
	private JLabel deviceLabel;
	private JList<String> deviceList;
	private JTextField counterField, valueField;

	public View() {
		// Make the frame.
		JFrame frame = new JFrame("CAM");

		final DefaultListModel<String> devices = new DefaultListModel<>();
		
		devices.addElement("Apple");
		devices.addElement("Grapes");
		devices.addElement("Mango");
		devices.addElement("Peer");
		
		deviceList = new JList<>(devices);
		
		// Setup device label + list.
		deviceLabel = new JLabel("Loading...");

		// Create panels and set the content pane.
		sidebar = new JPanel();
		sidebar.setLayout(new BorderLayout());
		sidebar.add(deviceLabel);
		JScrollPane left = new JScrollPane(sidebar);
		JScrollPane right = new JScrollPane();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		frame.setContentPane(splitPane);

		// Set size and make the window visible.
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Exit the program when the frame closes.
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	private void showDeviceList(boolean show) {
		if (show) {
			sidebar.add(deviceList);
		} else {
			sidebar.add(deviceLabel);
		}
		sidebar.revalidate();
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
