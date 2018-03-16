package com.ryansplayllc.ryansplay.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.AsyncTask;
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
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.LeaderBoardActivity;
import com.ryansplayllc.ryansplay.R;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.models.WorldPlayer;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

//import io.leocad.view.WebCachedImageView;

/**
 * Created by nimaikrsna on 5/3/2015.
 */
public class LeaderBoardList extends BaseAdapter
{
    LayoutInflater inflater;
    ArrayList<WorldPlayer> worldPlayerArrayList;
    Activity activity;
    public LeaderBoardList( Activity activity,ArrayList<WorldPlayer> worldPlayerArrayList) {


        Rollbar.setIncludeLogcat(true);

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.worldPlayerArrayList=worldPlayerArrayList;
        this.activity=activity;
    }


    @Override
    public int getCount() {
        return worldPlayerArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {
        TextView name;
        TextView rank;
        TextView pts;
        TextView ptsLabel;
        ImageView playerProfilePic;
        String imageUrl;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {

            ViewHolder viewHolder;


            if(convertView == null) {
                //world player items perform function
                convertView = inflater.inflate(
                        R.layout.worldleaderboardlist, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.playerName);
                viewHolder.rank = (TextView) convertView.findViewById(R.id.playerRank);
                viewHolder.pts = (TextView) convertView.findViewById(R.id.playerPts);
                viewHolder.ptsLabel = (TextView) convertView.findViewById(R.id.ptsLabel);


                viewHolder.playerProfilePic = (ImageView) convertView.findViewById(R.id.player_playerPic);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(worldPlayerArrayList.get(position).getUserName());

            if (LeaderBoardActivity.sortBy.equals("world_rank")) {
                viewHolder.rank.setText("#" + worldPlayerArrayList.get(position).getPointsRank() + "");
                viewHolder.pts.setText(worldPlayerArrayList.get(position).getPoints() + "");
                viewHolder.ptsLabel.setText("PTS");
            }

            if (LeaderBoardActivity.sortBy.equals("plays_rank")) {
                viewHolder.rank.setText("#" + worldPlayerArrayList.get(position).getPlaysRank() + "");
                viewHolder.pts.setText(worldPlayerArrayList.get(position).getPlayCoins() + "");
                viewHolder.ptsLabel.setText("PLAYS");

            }

            if (LeaderBoardActivity.sortBy.equals("biggest_game_won_rank")) {
                viewHolder.rank.setText("#" + worldPlayerArrayList.get(position).getBiggestGameWonRank() + "");
                viewHolder.pts.setText(worldPlayerArrayList.get(position).getBiggestGameWonPlayersCount() + "");
                viewHolder.ptsLabel.setText("PLAYERS");

            }

            Log.e("world players list length", worldPlayerArrayList.get(position).getUserName() + worldPlayerArrayList.get(position).getProfilePicURL() + "");

            viewHolder.imageUrl = worldPlayerArrayList.get(position).getProfilePicURL();



            UrlImageViewHelper.setUrlDrawable(viewHolder.playerProfilePic, worldPlayerArrayList.get(position).getProfilePicURL());


        }
        catch (Exception e)
        {

        }
        return convertView;

    }




}


