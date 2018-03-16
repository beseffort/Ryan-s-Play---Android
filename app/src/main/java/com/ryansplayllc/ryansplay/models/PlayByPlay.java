package com.ryansplayllc.ryansplay.models;

import org.json.JSONObject;

/**
 * Created by nimaikrsna on 5/22/2015.
 */
public class PlayByPlay
{
//    "comments" : "Home Run!, Basehit, Run Scored",
//            "game_id" : 2,
//            "play_number" : 1

    private String playbyPlayComments;
    private int gameId;
    private int playNo;
    public static String playByPlayGamePoints;

    public  String getPlayByPlayGamePoints() {
        return playByPlayGamePoints;
    }

    public static void setPlayByPlayGamePoints(String playByPlayGamePoints) {
        PlayByPlay.playByPlayGamePoints = playByPlayGamePoints;
    }

    public String getPlaybyPlayComments() {
        return playbyPlayComments;
    }

    public void setPlaybyPlayComments(String playbyPlayComments) {
        this.playbyPlayComments = playbyPlayComments;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPlayNo() {
        return playNo;
    }

    public void setPlayNo(int playNo) {
        this.playNo = playNo;
    }

    public void jsonParser(JSONObject playByPlayJsonObject)
    {
        try {


            if (playByPlayJsonObject.has("game_id")) {
                this.setGameId(playByPlayJsonObject.getInt("game_id"));

            }
            if (playByPlayJsonObject.has("play_number"))
            {
                this.setPlayNo(playByPlayJsonObject.getInt("play_number"));
            }

            if (playByPlayJsonObject.has("comments"))
            {
                this.setPlaybyPlayComments(playByPlayJsonObject.getString("comments"));
            }

        }
        catch (Exception e)
        {

        }
    }



}
