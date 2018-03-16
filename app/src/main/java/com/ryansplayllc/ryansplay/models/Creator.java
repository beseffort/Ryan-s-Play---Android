package com.ryansplayllc.ryansplay.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by nimaikrsna on 5/22/2015.
 */
public class Creator

{

    private long id;
    private String type;
    private static String UserName;
    private boolean status;
    private String creatorURL;
    private int noOfPlays;
    private Bitmap creatorImage = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCreatorURL() {
        return creatorURL;
    }

    public void setCreatorURL(String creatorURL) {
        this.creatorURL = creatorURL;
    }

    public int getNoOfPlays() {
        return noOfPlays;
    }

    public void setNoOfPlays(int noOfPlays) {
        this.noOfPlays = noOfPlays;
    }

    public Bitmap getCreatorImage() {
        return creatorImage;
    }

    public void setCreatorImage(Bitmap creatorImage) {
        this.creatorImage = getRoundedImage(creatorImage, 100, 100);
    }



    public Bitmap getRoundedUmpireImage() {
        if (this.creatorImage != null) {
            return getRoundedImage(this.creatorImage, 100, 100);
        }
        return null;
    }


    public void jsonParser(JSONObject creatorJsonObject) {
        try {
            Log.e("creator JsonParse4r","jons parsing");
            if (creatorJsonObject.has("id")) {
                this.setId(creatorJsonObject.getLong("id"));
            }
            if (creatorJsonObject.has("username")) {
                this.setUserName(creatorJsonObject.getString("username"));
            }
            if (creatorJsonObject.has("photo")) {
                Log.e("creator Image",creatorJsonObject.getString("photo"));
                this.setCreatorURL(creatorJsonObject.getString("photo"));
                Log.e("creator Image",getCreatorURL());
            }

            if (creatorJsonObject.has("status")) {
                this.setStatus(creatorJsonObject.getBoolean("status"));
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
}
