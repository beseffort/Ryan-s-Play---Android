package com.ryansplayllc.ryansplay.models;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by nimaikrsna on 6/11/2015.
 */
public class DisconnectedGame {

    private String player_game_playing_status;
    private int player_count;
    private long id;
    private int noOfPlays;
    private String game_type;
    private int no_of_plays_completed;
    private String game_status;



    public String getPlayer_game_playing_status() {
        return player_game_playing_status;
    }

    public void setPlayer_game_playing_status(String playingStatus) {
        player_game_playing_status = playingStatus;
    }

    public int getPlayer_count() {
        return player_count;
    }

    public void setPlayer_count(int count) {
        this.player_count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNoOfPlays() {
        return noOfPlays;
    }

    public void setNoOfPlays(int noOfPlays) {
        this.noOfPlays = noOfPlays;
    }

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String type) {
        this.game_type = type;
    }

    public int getNo_of_plays_completed() {
        return no_of_plays_completed;
    }

    public void setNo_of_plays_completed(int nop) {
        no_of_plays_completed = nop;
    }

    public String getGame_status() {
        return game_status;
    }

    public void setGame_status(String type) {
        this.game_status = type;
    }





    public void jsonParser(JSONObject disconnectedGameJsonObject,String status) {
        try {
            Log.e("creator JsonParse4r", "jons parsing");
            if(disconnectedGameJsonObject.has("player_game_playing_status")){
                this.setPlayer_game_playing_status(disconnectedGameJsonObject.getString("player_game_playing_status"));
            }
            if (disconnectedGameJsonObject.getJSONObject(status).has("id")) {
                this.setId(disconnectedGameJsonObject.getJSONObject(status).getLong("id"));
            }
            if (disconnectedGameJsonObject.getJSONObject(status).getJSONObject("private_game").has("no_of_plays")) {
                this.setNoOfPlays(disconnectedGameJsonObject.getJSONObject(status).getJSONObject("private_game").getInt("no_of_plays"));
            }
            if (disconnectedGameJsonObject.getJSONObject(status).has("game_type")) {
                this.setGame_type(disconnectedGameJsonObject.getJSONObject(status).getString("game_type"));
            }

            if (disconnectedGameJsonObject.getJSONObject(status).has("no_of_plays_completed")) {
                this.setNo_of_plays_completed(disconnectedGameJsonObject.getJSONObject(status).getInt("no_of_plays_completed"));
            }


        } catch (Exception exception) {
        }
    }


}
