package gameLogic.map;

public class Station{
	private String name;
	private IPositionable location;

	public Station(String name, IPositionable location){
		this.name = name;
		this.location = location;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public IPositionable getLocation() {
		return location;
	}
	public void setLocation(IPositionable location) {
		this.location = location;
	}
	
}
