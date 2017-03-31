import java.util.Observable;

public class Model extends Observable {
	private int counter;

	public Model() {
		counter = 0;
	}

	/**
	 * Get the counter.
	 * @return
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * Set the counter to the given value.
	 * @param count
	 */
	public void setCounter(int count) {
		counter = count;
		this.setChanged();
		this.notifyObservers(counter);
	}
}
