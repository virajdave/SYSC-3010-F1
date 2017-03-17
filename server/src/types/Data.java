package types;

public class Data {
	private String name;
	private String data;
	
	public Data(String name, String data) {
		this.name = name;
		this.data = data;
	}
	
	public Data(String name) {
		this.name = name;
		this.data = null;
	}
	
	public boolean is(String n) {
		return name.equals(n);
	}
	
	public String getName() {
		return name;
	}
	
	public String get() {
		return data;
	}
}
