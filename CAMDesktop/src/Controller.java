import java.awt.event.*;

public class Controller implements ActionListener {
	private Model model;
	private View view;

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (view.isResetButton(e.getSource())) {
			// If the reset button was pressed set the counter back to zero.
			model.setCounter(0);
		} else if (view.isSetButton(e.getSource())) {
			// If the set button was pressed set the counter back to the input value.
			String valueString = view.getValueField();
			try {
				int value = Integer.parseInt(valueString);
				model.setCounter(value);
			} catch (Exception ex) {
				System.out.println("The text is not a valid integer");
			}
		}
	}

}
