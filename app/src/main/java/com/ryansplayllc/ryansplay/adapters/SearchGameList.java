package com.ryansplayllc.ryansplay.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;

import com.ryansplayllc.ryansplay.R;
import com.ryansplayllc.ryansplay.Utility;
import com.ryansplayllc.ryansplay.models.Game;
import com.ryansplayllc.ryansplay.models.Umpire;

import java.util.List;

/**
 * Created by nimaikrsna on 5/14/2015.
 */
public class SearchGameList extends BaseAdapter
{
    private List<Game> games;
    private Activity activity;
    private LayoutInflater inflater;
    private Game game;
    private ImageView umpireProfilePicImageView;
    private int startPostion;
    private int gameListCode;

    private List<String> leftColorsList1;
    private List<String> rightColorsList1;


    private ImageView leftImage;
    private ImageView rightImage;

    public SearchGameList( Activity activity) {

        // roll bar
        Rollbar.setIncludeLogcat(true);

//        this.rightColorsList1=rightImageColorsList;
//        this.leftColorsList1=leftImageColorsList;


        this.activity = activity;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        game = Utility.game;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.joingame2_playerslist_item,
                    parent, false);
//
            TextView gameNameTextView = (TextView) convertView
                    .findViewById(R.id.joingame_gamename);

            gameNameTextView.setText(Utility.game.getGameName());

        }
        return convertView;
    }





    public Bitmap getRoundedImage(Bitmap image, int width, int height) {
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
