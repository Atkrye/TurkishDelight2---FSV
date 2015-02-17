package gameLogic.goal;

import java.util.ArrayList;

import Util.Tuple;
import gameLogic.map.Station;
import gameLogic.resource.Train;
import fvs.taxe.TaxeGame;

public class Goal {
	private int rewardScore;
	private Station origin;
	private Station destination;
	private int turnIssued;
	private boolean complete = false;
	//constraints
	private String trainName = null;
	//-1 Indicates currently unused for integer constraints
	private int turnCount = -1;
	private int trainCount = -1;
	private int locationCount = -1;
	private Station exclusionStation;
	private ArrayList<Train> completedTrains;
	private int constraintCount = 0;
	
	public Goal(Station origin, Station destination, int turn) {
		this.origin = origin;
		this.destination = destination;
		this.turnIssued = turn;
		setRewardScore();
	}
	
	public void addConstraint(String name, Object value) {
		System.out.println(name);
		if(name.equals("trainType")) {
			//CASE train type
			trainName = String.valueOf(value);
		}
		else if(name.equals("turnCount")) {
			//CASE turn count constraint
			int val = (Integer)value;
			//Ensure that our value is valid
			if(val >= 0)
			{
				turnCount = val;
			}
			else
			{
				throw new RuntimeException(val + " is not a valid turn count. Must be >= 0");
			}
		}
		else if(name.equals("trainCount")) {
			//CASE train count constraint
			int val = (Integer)value;
			//Ensure that our value is valid
			if(val >= 0)
			{
				trainCount = val;
				//If the completedTrains arraylist has not be initiated yet, initiate it
				if(completedTrains == null)
				{
					completedTrains = new ArrayList<Train>();
				}
			}
			else
			{
				throw new RuntimeException(val + " is not a valid train count. Must be >= 0");
			}
		} 
		else if(name.equals("exclusionStation")){
			exclusionStation = (Station)value;
		}
		else if(name.equals("locationCount"))
		{
			locationCount = (Integer)value;
		}
		else
		{
			throw new RuntimeException(name + " is not a valid goal constraint");
		}
		constraintCount ++;
	}
	
	private void setRewardScore() {
		int distX, distY;
		if (origin.getLocation().getX() < destination.getLocation().getX()){
			distX = destination.getLocation().getX() - origin.getLocation().getX();
		} else {
			distX = origin.getLocation().getX() - destination.getLocation().getX();
		}
		if (origin.getLocation().getY() < destination.getLocation().getY()){
			distY = destination.getLocation().getY() - origin.getLocation().getY();
		} else {
			distY = origin.getLocation().getY() - destination.getLocation().getY();
		}
		
		double dist = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY,2));
		double maxDist = Math.sqrt(Math.pow(TaxeGame.HEIGHT,2) + Math.pow(TaxeGame.WIDTH,2));
		//Scale score with route distant and number of constraints
		rewardScore = Math.round((float) (30 * (dist/maxDist) * (1 + constraintCount)));
	}

	public int getRewardScore() {
		return rewardScore;
	}

	public boolean isComplete(Train train) {
		boolean passedOrigin = false;
		boolean passedExclusion = false;
		int locationCountClone = -1;
		int originTurn = 0;
		for(Tuple<String, Integer> history: train.getHistory()) {
			if(history.getFirst().equals(origin.getName()) && history.getSecond() >= turnIssued) {
				passedOrigin = true;
				originTurn = history.getSecond();
				locationCountClone = locationCount;
			}
			else
			{
				if(locationCountClone > -1)
				{
					locationCountClone--;
				}
			}
		}
		if(exclusionStation != null && passedOrigin)
		{
			for(Tuple<String, Integer> history: train.getHistory()) {
				if(history.getFirst().equals(exclusionStation.getName()) && history.getSecond() >= originTurn) {
					passedExclusion = true;
				}
			}
		}
		if(train.getFinalDestination() == destination && passedOrigin && !passedExclusion && locationCountClone <= 1) {
			if(trainName == null || trainName.equals(train.getName())) 
			{
				//The train has completed the goal criteria. Determine whether it is a single or multiple criteria
				//And act accordingly
				if(trainCount != -1)
				{
					//Check if this a train we have already had complete this goal
					for(Train t : completedTrains)
					{
						if(t.equals(train))
						{
							return false;
						}
					}
					trainCount--;
					if(trainCount == 0)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					//No train count criteria. Return true
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean isFailed()
	{
		//We check our turn counter. If it is active, update
		if(turnCount != -1)
		{
			turnCount = turnCount - 2;
			if(turnCount <= 0)
			{
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		String trainString = "train";
		if(trainName != null) {
			trainString = trainName;
		}
		String turnString = "";
		if(turnCount != -1)
		{
			turnString = " within " + turnCount + " turns";
		}
		String trainCountString = "a ";
		if(trainCount != -1)
		{
			trainCountString = trainCount + "x ";
		}
		String exclusionString = "";
		if(exclusionStation != null)
		{
			exclusionString = " avoiding " + exclusionStation.getName() ;
		}
		String journeyString = "";
		if(locationCount != -1)
		{
			journeyString = " in less than " + locationCount + " journeys";
		}
		return "Send " + trainCountString + trainString + " from " + origin.getName() + " to " + destination.getName() + exclusionString + journeyString + turnString + " - " + rewardScore + " points";
	}

	public void setComplete() {
		complete = true;
	}

	public boolean getComplete() {
		return complete;
	}
	
	public Station getOrigin() {
		return this.origin;
	}
	
	public Station getDestination() {
		return this.destination;
	}
}