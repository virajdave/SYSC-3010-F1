import java.awt.event.*;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Controller implements ActionListener, ListSelectionListener {
	private Model model;
	private View view;
	private int selectedDevice = -1;

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;
		view.addListeners(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int index = view.getSelectedIndex();
		
		if (view.isRefreshButton(e.getSource())) {
			model.selectDevice(index);
		} else if (view.isDeleteButton(e.getSource())) {			
			model.deleteDevice(index);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void valueChanged(ListSelectionEvent event) {
		int index = view.getSelectedIndex();

		view.showRight(index != -1);
		if (selectedDevice != index && index != -1) {
			selectedDevice = index;
			model.selectDevice(selectedDevice);
		}
	}

}
