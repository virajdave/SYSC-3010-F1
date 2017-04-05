import java.awt.*;
import javax.swing.*;

import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
	private JPanel sidebar, right;
	private JLabel deviceLabel;
	private JTextArea textArea;
	private JList<String> deviceList;
	private JButton refresh, delete;
	private final DeviceListModel devices;
	JSplitPane splitPane;
	
	private static final double WEIGHT = 0.3; 

	public View() {
		// Make the frame.
		JFrame frame = new JFrame("CAM");

		devices = new DeviceListModel();
		
		deviceList = new JList<>(devices);
		deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Setup device label + list.
		deviceLabel = new JLabel();

		// Create panels and set the content pane.
		sidebar = new JPanel();
		sidebar.setLayout(new BorderLayout());
		sidebar.add(deviceLabel);
		JScrollPane left = new JScrollPane(sidebar);
		textArea = new JTextArea(5, 20);
		textArea.setEditable(false);
		
		JPanel menu = new JPanel(new GridLayout(1, 2));
		refresh = new JButton("Refresh");
		delete = new JButton("Delete");
		menu.add(refresh);
		menu.add(delete);
		right = new JPanel(new BorderLayout());
		right.add(BorderLayout.NORTH, menu);
		right.add(BorderLayout.CENTER, new JScrollPane(textArea));
		right.setVisible(false);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		splitPane.setResizeWeight(WEIGHT);
		frame.setContentPane(splitPane);

		// Set size and make the window visible.
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Exit the program when the frame closes.
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public DeviceListModel getDeviceModel() {
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
			
			if (s.length() == 0) {
				boolean empty = devices.isEmpty();
				showDeviceList(!empty);
				if (empty) {
					deviceLabel.setText("No devices.");
				}
			} else if(s.charAt(0) == '[') {
				textArea.setText(s.substring(1));
			} else {
				deviceLabel.setText(s);
			}
		}
	}
	
	public boolean isRefreshButton(Object obj) {
		return refresh.equals(obj);
	}
	
	public boolean isDeleteButton(Object obj) {
		return delete.equals(obj);
	}
	
	public int getSelectedIndex() {
		return deviceList.getSelectedIndex();
	}

	/**
	 * Add button listeners to the controller.
	 * 
	 * @param controller
	 */
	public void addListeners(Controller controller) {
		deviceList.addListSelectionListener(controller);
		refresh.addActionListener(controller);
		delete.addActionListener(controller);
	}

	public void showRight(boolean b) {
		if (b && !right.isVisible()) {
			splitPane.setDividerLocation(WEIGHT);
		}
		right.setVisible(b);
	}

}
