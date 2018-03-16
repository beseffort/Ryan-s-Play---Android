package com.ryansplayllc.ryansplay.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

import org.json.JSONObject;

public class WorldPlayer {

    private long id;
    private String firstName;
    private String lastName;
    private String userType;
    private Bitmap profilePic = null;
    private String userName;
    private long playCoins;
    private long points;
    private long playsCount;
    private String biggestGameWon;
    private long biggestGameWonPlayersCount;
    private long playsRank;
    private long pointsRank;
    private long biggestGameWonRank;
    private String profilePicURL;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public long getPlayCoins() {
        return playCoins;
    }

    public void setPlayCoins(long playCoins) {
        this.playCoins = playCoins;
    }

    public long getPoints() {   return points;    }

    public void setPoints(long points) {  this.points = points;    }

    public long getPlaysCount() {   return playsCount;    }

    public void setPlaysCount(long playsCount) {  this.playsCount = playsCount;    }

    public String getBiggestGameWon() {
        return biggestGameWon;
    }

    public void setBiggestGameWon(String biggestGameWon) {
        this.biggestGameWon = biggestGameWon;
    }

    public long getBiggestGameWonPlayersCount() {   return biggestGameWonPlayersCount;    }

    public void setBiggestGameWonPlayersCount(long biggestGameWonPlayersCount) {  this.biggestGameWonPlayersCount = biggestGameWonPlayersCount;    }

    public long getPlaysRank() {   return playsRank;    }

    public void setPlaysRank(long playsRank) {  this.playsRank = playsRank;    }

    public long getPointsRank() {   return pointsRank;    }

    public void setPointsRank(long pointsRank) {  this.pointsRank = pointsRank;    }

    public long getBiggestGameWonRank() {   return biggestGameWonRank;    }

    public void setBiggestGameWonRank(long biggestGameWonRank) {  this.biggestGameWonRank = biggestGameWonRank;    }





    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = getRoundedProfileImage(profilePic, 100, 100);
    }



    private Bitmap getRoundedProfileImage(Bitmap profileImage, int width,
                                          int height) {
        int targetWidth = width;
        int targetHeight = height;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = profileImage;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
                targetHeight), null);
        return targetBitmap;
    }


    public void jsonParser(JSONObject userJsonObject) {

        try {
            this.setId(userJsonObject.getLong("id"));
            this.setFirstName(userJsonObject.getString("first_name"));
            this.setLastName(userJsonObject.getString("last_name"));
            this.setUserName(userJsonObject.getString("username"));
            this.setUserType(userJsonObject.getString("user_type"));
            this.setProfilePicURL(userJsonObject.getString("photo"));
            this.setPlayCoins(userJsonObject.getLong("play_coins"));
            this.setPoints(userJsonObject.getLong("points"));
            this.setPlaysCount(userJsonObject.getLong("plays_count"));
            this.setBiggestGameWon(userJsonObject.getString("biggest_game_won_name"));
            this.setBiggestGameWonPlayersCount(userJsonObject.getLong("biggest_game_won_players_count"));
            this.setPlaysRank(userJsonObject.getLong("plays_rank"));
            this.setPointsRank(userJsonObject.getLong("points_rank"));
            this.setBiggestGameWonRank(userJsonObject.getLong("biggest_game_won_rank"));
        } catch (Exception exception) {
        }
    }



}
