package com.ryansplayllc.ryansplay.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.GameLobbyActivity;
import com.ryansplayllc.ryansplay.R;
import com.ryansplayllc.ryansplay.Utility;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.services.GameService;

public class GameLobbyPlayerList extends BaseAdapter {

   public List<Player> players;
    Activity activity;
    LayoutInflater inflater;
    Player player;

    public GameLobbyPlayerList(List<Player> players, Activity activity) {

        // roll bar
        Rollbar.setIncludeLogcat(true);


        this.players = players;
        this.activity = activity;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int position) {

        return players.get(position);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder
    {
        ImageView profilePicImageView;
        TextView nameTextView ;
        TextView noOfPlaysTextView;
        ImageView umpireMaskImage ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate( R.layout.game_lobby_player_list_item, parent, false);
                viewHolder = new ViewHolder();

                viewHolder. profilePicImageView = (ImageView) convertView.findViewById(R.id.pl_iv_profile_pic);
                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.pl_tv_player_name);
                viewHolder.noOfPlaysTextView = (TextView) convertView.findViewById(R.id.pl_tv_no_of_plays);
                viewHolder. umpireMaskImage = (ImageView) convertView.findViewById(R.id.umpiremask);

                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            player = (Player) getItem(position);


            UrlImageViewHelper.setUrlDrawable(viewHolder.profilePicImageView, player.getPictureUrl());

            viewHolder.nameTextView.setText(player.getUsername());
            viewHolder.noOfPlaysTextView.setText(Integer.toString(player.getNoOfPlays()));
            Log.e("new umpire username",Utility.game.getUmpire().getUserName()+"umpire id="+Utility.game.getId());
            if(player.getUserId()==Utility.game.getUmpire().getId())
            {
                viewHolder.umpireMaskImage.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.umpireMaskImage.setVisibility(View.GONE);
            }


    }
    catch (Exception e)
    {

    }
        return convertView;
    }

    public void remove(int position)
    {

        players.remove(players.get(position));
        GameLobbyActivity.gameLobbyPlayersSize.setText("["+ players.size()+"]");


    }
    public void remove2()
    {

        notifyDataSetChanged();



    }

}
