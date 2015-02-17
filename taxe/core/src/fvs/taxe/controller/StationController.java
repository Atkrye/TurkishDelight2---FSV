package fvs.taxe.controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import fvs.taxe.Tooltip;
import fvs.taxe.actor.CollisionStationActor;
import fvs.taxe.actor.ConnectionActor;
import fvs.taxe.actor.StationActor;
import fvs.taxe.dialog.DialogStationMultitrain;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

public class StationController {
	public final static int CONNECTION_LINE_WIDTH = 5;

	private Context context;
	private Tooltip tooltip;

	/*
    have to use CopyOnWriteArrayList because when we iterate through our listeners and execute
    their handler's method, one case unsubscribes from the event removing itself from this list
    and this list implementation supports removing elements whilst iterating through it
	 */
	private static List<StationClickListener> stationClickListeners = new CopyOnWriteArrayList<StationClickListener>();

	public StationController(Context context, Tooltip tooltip) {
		this.context = context;
		this.tooltip = tooltip;
	}

	public static void subscribeStationClick(StationClickListener listener) {
		stationClickListeners.add(listener);
	}

	public static void unsubscribeStationClick(StationClickListener listener) {
		stationClickListeners.remove(listener);
	}

	private static void stationClicked(Station station) {
		for (StationClickListener listener : stationClickListeners) {
			listener.clicked(station);
		}
	}

	private void renderStation(final Station station) {
		final StationActor stationActor = new StationActor(station.getLocation());

		stationActor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(Game.getInstance().getState() == GameState.NORMAL){
					DialogStationMultitrain dia = new DialogStationMultitrain(station, context.getSkin(), context);
					if(dia.getHasTrain()) {
						dia.show(context.getStage());
					}
				}
				stationClicked(station);
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				tooltip.setPosition(stationActor.getX() + 20, stationActor.getY() + 20);
				tooltip.show(station.getName());
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				tooltip.hide();
			}
		});
		station.setActor(stationActor);
		context.getStage().addActor(stationActor);
	}

	private void renderCollisionStation(final CollisionStation collisionStation) {
		final CollisionStationActor collisionStationActor = new CollisionStationActor(collisionStation.getLocation());

		collisionStationActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stationClicked(collisionStation);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                tooltip.setPosition(collisionStationActor.getX() + 10, collisionStationActor.getY() + 10);
                tooltip.show("Junction: " + collisionStation.getName());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                tooltip.hide();
            }
        });
		context.getStage().addActor(collisionStationActor);
	}

	public void drawStations() {
		List<Station> stations = context.getGameLogic().getMap().getStations();

		for (Station station : stations) {
			if(station instanceof CollisionStation) {
				renderCollisionStation((CollisionStation) station);
			} else {
				renderStation(station);
			}
		}
	}

	public void drawConnections(List<Connection> connections, final Color color) {
		for (Connection connection : connections) {
			final IPositionable start = connection.getStation1().getLocation();
			final IPositionable end = connection.getStation2().getLocation();
			ConnectionActor connectionActor = new ConnectionActor(Color.GRAY, start, end, CONNECTION_LINE_WIDTH);
			connection.setActor(connectionActor);
			context.getStage().addActor(connectionActor);
		}
	}

	public void displayNumberOfTrainsAtStations() {
		TaxeGame game = context.getTaxeGame();
		game.fontSmall.setColor(Color.BLACK);
		game.batch.begin();
		for(Station station : context.getGameLogic().getMap().getStations()) {
			if(trainsAtStation(station) > 0) {
				game.fontSmall.draw(game.batch, trainsAtStation(station) + "", (float) station.getLocation().getX() - 6, (float) station.getLocation().getY() + 26);
			}
		}
		game.batch.end();
	}

	private int trainsAtStation(Station station) {
		int count = 0;

		Player player = context.getGameLogic().getPlayerManager().getCurrentPlayer();
		for(Resource resource : player.getResources()) {
			if(resource instanceof Train) {
				if(((Train) resource).getActor() != null) {
					if(((Train) resource).getPosition().equals(station.getLocation())) {
						count++;
					}
				}
			}
		}
		return count;
	}
}
