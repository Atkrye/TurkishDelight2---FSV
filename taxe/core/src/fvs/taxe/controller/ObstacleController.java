package fvs.taxe.controller;

import fvs.taxe.actor.ObstacleActor;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;

import java.util.ArrayList;

import Util.Tuple;

public class ObstacleController {

	private Context context;
	
	public ObstacleController(Context context) {
		// take care of rendering of stations (only rendered on map creation, visibility changed when active)
		this.context = context;
		context.getGameLogic().subscribeObstacleChanged(new ObstacleListener() {
			
			@Override
			public void started(Obstacle obstacle) {
				obstacle.start();
				obstacle.getStation().setObstacle(obstacle); 
			}
			
			@Override
			public void ended(Obstacle obstacle) {
				obstacle.getStation().clearObstacle();
				obstacle.end();
			}
		});
	}
	
	public void drawObstacles(){
		// needs to only be called once, on map creation
		// adds all obstacles to the stage but makes them invisible
		ArrayList<Tuple<Obstacle, Float>> obstaclePairs = context.getGameLogic().getObstacleManager().getObstacles();
		for (Tuple<Obstacle, Float> obstaclePair: obstaclePairs) {
			renderObstacle(obstaclePair.getFirst(), false);
		}
	}

	private ObstacleActor renderObstacle(Obstacle obstacle, boolean visible) {
		// render the obstacle's actor with the visibility given
		ObstacleActor obstacleActor = new ObstacleActor(obstacle);
		obstacleActor.setVisible(visible);
		obstacle.setActor(obstacleActor);
		context.getStage().addActor(obstacleActor);
		return obstacleActor;
	}
	
}
