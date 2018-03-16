package com.ryansplayllc.ryansplay.models;

import org.json.JSONObject;

import android.graphics.Bitmap;

public class User {

    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String userName;
    private String provoider;
    private String profilePicURL;
    private Bitmap profilePic = null;
    private Player player = new Player();
    private String accessToken;
    private String birthday;
    private String gender;
    private String rpCode;





    public String getRpCode() {
        return rpCode;
    }

    public void setRpCode(String rpCode) {
        this.rpCode = rpCode;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    private static String userLastRank;
    private static String lastGameName;
    private static String lastGameCreator;
    private static String lastGamePoints;
    private static String lastGamePlays;
    public static  String totalNoOfPlays;

    public  String getTotalNoOfPlays() {
        return totalNoOfPlays;
    }

    public  void setTotalNoOfPlays(String totalNoOfPlays) {
        User.totalNoOfPlays = totalNoOfPlays;
    }

    public String getLastGamePoints() {
        return lastGamePoints;
    }

    public void setLastGamePoints(String lastGamePoints) {
        this.lastGamePoints = lastGamePoints;
    }

    public String getLastGamePlays() {
        return lastGamePlays;
    }

    public void setLastGamePlays(String lastGamePlays) {
        this.lastGamePlays = lastGamePlays;
    }

    public String getLastGameCreator() {
        return lastGameCreator;
    }

    public void setLastGameCreator(String lastGameCreator) {
        this.lastGameCreator = lastGameCreator;
    }

    public String getLastGameName() {
        return lastGameName;
    }

    public void setLastGameName(String lastGameName) {
        this.lastGameName = lastGameName;
    }

    public String getUserRank() {
        return userLastRank;
    }

    public void setUserRank(String userRank) {
        this.userLastRank = userRank;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProvoider() {
        return provoider;
    }

    public void setProvoider(String provoider) {
        this.provoider = provoider;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
        this.profilePic = profilePic;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void jsonParser(JSONObject userJsonObject) {

        try {
            this.setId(userJsonObject.getLong("id"));
            this.setEmail(userJsonObject.getString("email"));
            this.setUserName(userJsonObject.getString("username"));
            this.setFirstName(userJsonObject.getString("first_name"));
            this.setLastName(userJsonObject.getString("last_name"));
            this.setProfilePicURL(userJsonObject.getString("photo"));
            this.setProvoider(userJsonObject.getString("provider"));
            this.getPlayer().setPlayCoins(Integer.parseInt(userJsonObject.getString("play_coins")));
            this.setTotalNoOfPlays(userJsonObject.getString("play_coins"));
            player.jsonParser(userJsonObject.getJSONObject("player"));
        } catch (Exception exception) {
        }
    }
}
