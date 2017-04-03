
public class Main {

	public static void main(String[] args) {
		View view = new View();
		Model model = new Model(view.getDeviceModel());

		model.addObserver(view);
		Controller controller = new Controller(model, view);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		model.start();
	}

}
