import java.awt.*;
import javax.swing.*;

import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
	private JPanel sidebar;
	private JLabel deviceLabel;
	private JTextArea textArea;
	private JList<String> deviceList;
	private final DefaultListModel<String> devices;

	public View() {
		// Make the frame.
		JFrame frame = new JFrame("CAM");

		devices = new DefaultListModel<>();
		
		devices.addElement("Apple");
		devices.addElement("Grapes");
		devices.addElement("Mango");
		devices.addElement("Peer");
		
		deviceList = new JList<>(devices);
		deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Setup device label + list.
		deviceLabel = new JLabel("                                                  ");

		// Create panels and set the content pane.
		sidebar = new JPanel();
		sidebar.setLayout(new BorderLayout());
		sidebar.add(deviceLabel);
		JScrollPane left = new JScrollPane(sidebar);
		textArea = new JTextArea(5, 20);
		textArea.setEditable(false);
		JScrollPane right = new JScrollPane(textArea);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		splitPane.setResizeWeight(0.2);
		frame.setContentPane(splitPane);

		// Set size and make the window visible.
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Exit the program when the frame closes.
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public DefaultListModel<String> getDeviceModel() {
		return devices;
	}
	
	private void showDeviceList(boolean show) {
		if (show) {
			sidebar.add(deviceList);
			sidebar.remove(deviceLabel);
		} else {
			sidebar.add(deviceLabel);
			sidebar.remove(deviceList);
		}
		sidebar.revalidate();
	}

	@Override
	public void update(Observable model, Object obj) {
		if (obj instanceof String) {
			String s = (String)obj;
			
			deviceLabel.setText(s);
			if (devices.isEmpty()) {
				deviceLabel.setText("No devices.");
				showDeviceList(false);
			} else {
				showDeviceList(s.length() == 0);
			}
			
			if (s.length() > 0 && s.charAt(0) == '[') {
				textArea.setText(s.substring(1));
			}
		}
	}

	/**
	 * Add button listeners to the controller.
	 * 
	 * @param controller
	 */
	public void addListeners(Controller controller) {
		deviceList.addListSelectionListener(controller);
	}

}
