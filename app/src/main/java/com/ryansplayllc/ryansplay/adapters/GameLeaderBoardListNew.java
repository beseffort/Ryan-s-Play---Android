package com.ryansplayllc.ryansplay.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.ryansplayllc.ryansplay.R;
import com.ryansplayllc.ryansplay.Utility;
import com.ryansplayllc.ryansplay.models.Player;

import java.util.List;

/**
 * Created by nimaikrsna on 5/19/2015.
 */
public class GameLeaderBoardListNew extends BaseAdapter {

    List<Player> playerList;
    Activity activity;
    LayoutInflater inflater;
    public GameLeaderBoardListNew(Activity activity, List<Player> playerList)
    {
        this.activity = activity;
        Rollbar.setIncludeLogcat(true);

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.playerList=playerList;
    }

    @Override
    public int getCount() {
        return playerList.size();
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
        TextView listPlayerName    ;
        TextView listPlayerPoints  ;
        TextView worldRank         ;
        ImageView finalLeaderBoardProfileImage;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            ViewHolder viewHolder;
            if(convertView == null) {
                convertView = inflater.inflate(
                        R.layout.game_leader_board_player_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.listPlayerName = (TextView) convertView.findViewById(R.id.glbi_tv_player_name);
                viewHolder.listPlayerPoints = (TextView) convertView.findViewById(R.id.glbi_tv_world_rank);
                viewHolder.worldRank = (TextView) convertView.findViewById(R.id.glbi_tv_player_rank);
                viewHolder. finalLeaderBoardProfileImage = (ImageView) convertView.findViewById(R.id.glbi_iv_profile_pic);
                convertView.setTag(viewHolder);

            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            Integer points = playerList.get(position).getTotalPoints();
            viewHolder.listPlayerName.setText(playerList.get(position).getUsername());


            viewHolder.listPlayerPoints.setText(points + "");

            //loading current player profile image
            Log.e("userimageurl", Utility.user.getProfilePicURL() + "");


            UrlImageViewHelper.setUrlDrawable(viewHolder.finalLeaderBoardProfileImage, playerList.get(position).getPictureUrl());


            viewHolder.worldRank.setText("# " + playerList.get(position).getWorldRank() + "");
        }
        catch (Exception e)
        {

        }
        return convertView;

    }


}
