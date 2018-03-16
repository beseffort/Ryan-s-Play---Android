package com.ryansplayllc.ryansplay.models;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

public class Player {

	private long playerId;
	private long userId;
	private String username;
	private String pictureUrl;
	private int noOfPlays;
	private int totalPoints;
	private int playsCompleted;
	private int worldRank;
	private int hr;
	private int basehits;
	private int strikeout;
	private int playCoins;
	private Bitmap profileImage;
	private int gamepoints;
    private int rings;
    private int accuracy;
    private String gender;
    private String city;
    private String state;
    private String birthday;
    private String zipcode;
    private  String pointsRank;
    private String  ringsRank;
    private String  accuracyRank;
    private String  hitsRank;
    private String  homeRunsRank;
    private String  scoreRunRank;
    private String gameRank;
    private String playsRank;

    public String getPointsRank() {
        return pointsRank;
    }

    public void setPointsRank(String pointsRank) {
        this.pointsRank = pointsRank;
    }

    public String getRingsRank() {
        return ringsRank;
    }

    public void setRingsRank(String ringsRank) {
        this.ringsRank = ringsRank;
    }

    public String getAccuracyRank() {
        return accuracyRank;
    }

    public void setAccuracyRank(String accuracyRank) {
        this.accuracyRank = accuracyRank;
    }

    public String getHitsRank() {
        return hitsRank;
    }

    public void setHitsRank(String hitsRank) {
        this.hitsRank = hitsRank;
    }

    public String getHomeRunsRank() {
        return homeRunsRank;
    }

    public void setHomeRunsRank(String homeRunsRank) {
        this.homeRunsRank = homeRunsRank;
    }

    public String getScoreRunRank() {
        return scoreRunRank;
    }

    public void setScoreRunRank(String scoreRunRank) {
        this.scoreRunRank = scoreRunRank;
    }

    public String getGameRank() {
        return gameRank;
    }

    public void setGameRank(String gameRank) {
        this.gameRank = gameRank;
    }

    public String getPlaysRank() {
        return playsRank;
    }

    public void setPlaysRank(String playsRank) {
        this.playsRank = playsRank;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public int getRings() {
        return rings;
    }

    public void setRings(int rings) {
        this.rings = rings;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }



    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}

	public int getPlaysCompleted() {
		return playsCompleted;
	}

	public void setPlaysCompleted(int playsComplete) {
		this.playsCompleted = playsComplete;
	}

	public int getHr() {
		return hr;
	}

	public void setHr(int hr) {
		this.hr = hr;
	}

	public int getBasehits() {
		return basehits;
	}

	public void setBasehits(int basehits) {
		this.basehits = basehits;
	}

	public int getPlayCoins() {
		return playCoins;
	}

	public void setPlayCoins(int playCoins) {
		this.playCoins = playCoins;
	}

	public int getGamepoints() {
		return gamepoints;
	}

	public void setGamepoints(int gamepoints) {
		this.gamepoints = gamepoints;
	}

	public Bitmap getProfileImage() {
		return profileImage;
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

	public void setProfileImage(Bitmap profileImage) {
		this.profileImage = getRoundedProfileImage(profileImage, 100, 100);
	}

	public int getNoOfPlays() {
		return noOfPlays;
	}

	public void setNoOfPlays(int noOfPlays) {
		this.noOfPlays = noOfPlays;
	}

	public void setName(String username) {
		this.username = username;
	}

	public int getWorldRank() {
		return worldRank;
	}

	public void setWorldRank(int worldRank) {
		this.worldRank = worldRank;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getStrikeout() {
		return strikeout;
	}

	public void setStrikeout(int strikeout) {
		this.strikeout = strikeout;
	}

	public void jsonParser(JSONObject playerJsonObject) {
		try {

            if (playerJsonObject.has("id")) {
				this.setUserId(playerJsonObject.getLong("id"));
			}
			if (playerJsonObject.has("plays_in_hand")) {
				this.setNoOfPlays(playerJsonObject.getInt("plays_in_hand"));
			}
			// in game lobby
			if (playerJsonObject.has("play_coins")) {
				this.setNoOfPlays(playerJsonObject.getInt("play_coins"));
			}
			if (playerJsonObject.has("username")) {
				this.setUsername(playerJsonObject.getString("username"));
			}
			if (playerJsonObject.has("photo")) {
				this.setPictureUrl(playerJsonObject.getString("photo"));
			}
			if (playerJsonObject.has("player_id")) {
				this.setPlayerId(playerJsonObject.getLong("player_id"));
			}
			if (playerJsonObject.has("points_in_game")) {
				this.setTotalPoints(playerJsonObject
						.getInt("points_in_game"));
			}
			if (playerJsonObject.has("plays_completed")) {
				this.setPlaysCompleted(playerJsonObject
						.getInt("plays_completed"));
			}
			if (playerJsonObject.has("plays_rank")) {
				this.setWorldRank(playerJsonObject.getInt("plays_rank"));
			}
            if (playerJsonObject.has("rank")) {
                this.setWorldRank(playerJsonObject.getInt("rank"));
            }
			if (playerJsonObject.has("homerun_count")) {
				this.setHr(playerJsonObject.getInt("homerun_count"));
			}
			if (playerJsonObject.has("basehit_count")) {
				this.setBasehits(playerJsonObject.getInt("basehit_count"));
			}
			if (playerJsonObject.has("runscored_count")) {
				this.setStrikeout(playerJsonObject.getInt("runscored_count"));
			}
			if (playerJsonObject.has("points")) {
				this.setGamepoints(playerJsonObject.getInt("points"));
			}
            if (playerJsonObject.has("points_in_game")) {
                this.setGamepoints(playerJsonObject.getInt("points_in_game"));
            }
            if (playerJsonObject.has("rings_count")) {
				this.setRings(playerJsonObject.getInt("rings_count"));
			}
            if (playerJsonObject.has("accuracy")) {
				this.setAccuracy(playerJsonObject.getInt("accuracy"));
			}

            if (playerJsonObject.has("accuracy")) {
				this.setAccuracy(playerJsonObject.getInt("accuracy"));
			}
if (playerJsonObject.has("plays_rank")) {
				this.setPlaysRank(playerJsonObject.getString("plays_rank"));
			}
if (playerJsonObject.has("games_rank")) {
				this.setGameRank(playerJsonObject.getString("games_rank"));
			}
if (playerJsonObject.has("homerun_rank")) {
				this.setHomeRunsRank(playerJsonObject.getString("homerun_rank"));
			}
if (playerJsonObject.has("basehit_rank")) {
				this.setHitsRank(playerJsonObject.getString("basehit_rank"));
			}
if (playerJsonObject.has("runscore_rank")) {
				this.setScoreRunRank(playerJsonObject.getString("runscore_rank"));
			}
if (playerJsonObject.has("points_rank")) {
				this.setPointsRank(playerJsonObject.getString("points_rank"));
			}

            if (playerJsonObject.has("city")) {
				this.setCity(playerJsonObject.getString("city"));
			}if (playerJsonObject.has("state")) {
				this.setState(playerJsonObject.getString("state"));
			}if (playerJsonObject.has("zipcode")) {
				this.setZipcode(playerJsonObject.getString("zipcode"));
			}if (playerJsonObject.has("gender")) {
				this.setGender(playerJsonObject.getString("gender"));
			}if (playerJsonObject.has("birthday")) {
				this.setBirthday(playerJsonObject.getString("birthday"));
			}
		} catch (Exception exception) {
		}
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
