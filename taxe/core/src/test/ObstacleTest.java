package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleType;

import org.junit.Before;
import org.junit.Test;

public class ObstacleTest extends LibGdxTest {

	Obstacle obstacle;
	
	@Before
	public void ObstacleSetup() throws Exception {
		Station station = new Station("station", new Position(5, 5));
		obstacle = new Obstacle(ObstacleType.VOLCANO, station);
	}
	
	@Test
	public void obstacleTest()  {
		Station station = new Station("station", new Position(5, 5));
		obstacle = new Obstacle(ObstacleType.VOLCANO, station);
		assertEquals(station, obstacle.getStation());
	}
	
	@Test
	public void getDestructionChanceTest() {
		assertEquals(0.1f, obstacle.getDestructionChance(), 2);
	}
	
	@Test
	public void timeDecreasingTest() {
		obstacle.start();
		obstacle.decreaseTimeLeft();
		obstacle.getTimeLeft();
		assertEquals(1, obstacle.getTimeLeft());
	}
	
	@Test
	public void startTest() {
		obstacle.start();
		assertEquals(2,obstacle.getTimeLeft());
		assertTrue(obstacle.isActive());
	}

}
