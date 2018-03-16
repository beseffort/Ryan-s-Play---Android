package com.ryansplayllc.ryansplay.models;

import android.util.Log;

import org.json.JSONObject;

public class Game {
	private long id;
	private String gameName;
	private int length;
	private int gameStatus;
	private int points;
	private String gameType;
	private int noOfPlayers;
	private int playsCompleted;
	private Umpire umpire = new Umpire();
	private Player gameCreator = new Player();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(int gameStatus) {
		this.gameStatus = gameStatus;
	}

	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public Umpire getUmpire() {
		return umpire;
	}

	public void setUmpire(Umpire umpire) {
		this.umpire = umpire;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {

        Log.e("setting game length ", "length " + length);
		this.length = length;
	}

	public int getNoOfPlayers() {
		return noOfPlayers;
	}

	public void setNoOfplayers(int no_of_players) {
		this.noOfPlayers = no_of_players;
	}

	public int getPlaysCompleted() {
		return playsCompleted;
	}

	public void setPlaysCompleted(int playsCompleted) {
		this.playsCompleted = playsCompleted;
	}

	public Player getGameCreator() {
		return gameCreator;
	}

	public void setGameCreator(Player gameCreator) {
		this.gameCreator = gameCreator;
	}

	public void JsonParser(JSONObject gameJsonObject) {
		try {
			if (gameJsonObject.has("id")) {
				this.setId(gameJsonObject.getLong("id"));
			}
			if (gameJsonObject.has("name")) {
				this.setGameName(gameJsonObject.getString("name"));
			}
			if (gameJsonObject.has("no_of_plays")) {
				this.setLength(gameJsonObject.getInt("no_of_plays"));
			}
			if (gameJsonObject.has("length")) {
				this.setLength(gameJsonObject.getInt("length"));
			}
			if (gameJsonObject.has("plays_completed")) {
				this.setPlaysCompleted(gameJsonObject.getInt("plays_completed"));
			}

			if (gameJsonObject.has("umpire")) {
				this.umpire.jsonParser(gameJsonObject.getJSONObject("umpire"));
			}
			if (gameJsonObject.has("number_of_players")) {
				this.setNoOfplayers(gameJsonObject.getInt("number_of_players"));
			}
			if (gameJsonObject.has("game_status")) {
				this.setGameStatus(gameJsonObject.getInt("game_status"));
			}
			if (gameJsonObject.has("owner")) {
				this.gameCreator.jsonParser(gameJsonObject
						.getJSONObject("owner"));
                Log.e("owner object",gameJsonObject.toString());
			}
		} catch (Exception exception) {
		}
	}

}
