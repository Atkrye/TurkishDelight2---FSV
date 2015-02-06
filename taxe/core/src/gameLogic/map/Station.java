package gameLogic.map;

import fvs.taxe.actor.StationActor;
import gameLogic.obstacle.Obstacle;

public class Station{
	private String name;
	private IPositionable location;
	private StationActor actor;
	private Obstacle obstacle;
	
	public Station(String name, IPositionable location) {
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
	
	public void setActor(StationActor actor){
		this.actor = actor;
	}
	
	public StationActor getActor(){
		return actor;
	}

	public void setObstacle(Obstacle obstacle) {
		this.obstacle = obstacle;
	}

	public boolean hasObstacle(){
		if (this.obstacle == null){
			return false;
		} else {
			return true;
		}
	}
	
	public Obstacle getObstacle(){
		return this.obstacle;
	}
	
	public void clearObstacle() {
		this.obstacle = null;
	}
	
}
