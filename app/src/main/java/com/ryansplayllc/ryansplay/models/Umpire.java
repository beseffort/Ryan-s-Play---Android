package com.ryansplayllc.ryansplay.models;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

import com.ryansplayllc.ryansplay.Utility;

public class Umpire {

	private long id;
	private String type;
	private boolean status;
	private long umpirable_id;
	private String umpireURL;
	private int noOfPlays;
	private Bitmap umpireImage = null;

	private String userName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
        Log.e("** umpire change list --> " + id +"=="+getId()+"***", Utility.user.getId() +" new umpire id---> " + Utility.game.getUmpire().getId());
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public long getUmpirable_id() {
		return umpirable_id;
	}

	public void setUmpirable_id(long umpirable_id) {
		this.umpirable_id = umpirable_id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Bitmap getUmpireImage() {
		return umpireImage;
	}

	public Bitmap getRoundedUmpireImage() {
		if (this.umpireImage != null) {
			return getRoundedImage(this.umpireImage, 100, 100);
		}
		return null;
	}

	public void setUmpireImage(Bitmap umpireImage) {
		this.umpireImage = umpireImage;
	}

	public String getUmpireURL() {
		return umpireURL;
	}

	public void setUmpireURL(String umpireURL) {
		this.umpireURL = umpireURL;
	}

	// /

	public void jsonParser(JSONObject umpireJsonObject) {
		try {
			if (umpireJsonObject.has("id")) {
				this.setId(umpireJsonObject.getLong("id"));
			}
			if (umpireJsonObject.has("username")) {
				this.setUserName(umpireJsonObject.getString("username"));
			}
			if (umpireJsonObject.has("photo")) {
				this.setUmpireURL(umpireJsonObject.getString("photo"));
			}
			if (umpireJsonObject.has("play_coins")) {
				this.setNoOfPlays(umpireJsonObject.getInt("play_coins"));
			}
			if (umpireJsonObject.has("status")) {
				this.setStatus(umpireJsonObject.getBoolean("status"));
			}
		} catch (Exception exception) {
		}
	}

	private Bitmap getRoundedImage(Bitmap image, int width, int height) {
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
		Bitmap sourceBitmap = image;
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
				targetHeight), null);
		return targetBitmap;
	}

	public int getNoOfPlays() {
		return noOfPlays;
	}

	public void setNoOfPlays(int noOfPlays) {
		this.noOfPlays = noOfPlays;
	}

}
