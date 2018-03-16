package com.ryansplayllc.ryansplay.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.R;
import com.ryansplayllc.ryansplay.models.PlayByPlay;
import com.ryansplayllc.ryansplay.models.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nimaikrsna on 5/23/2015.
 */
public class UmpirePlayByPlayAdapter extends BaseAdapter
{
    List<Player> players;
    Activity activity;
    LayoutInflater inflater;
    ArrayList<PlayByPlay> playByPlayList;


    public UmpirePlayByPlayAdapter(Activity activity, ArrayList<PlayByPlay> playByPlayList) {
        this.activity = activity;
        Rollbar.setIncludeLogcat(true);

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.playByPlayList=playByPlayList;
    }

    @Override
    public int getCount() {
        return playByPlayList.size();

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(
                R.layout.umpire_play_by_play_list_item, parent, false);
        TextView playNo= (TextView) convertView.findViewById(R.id.playNo);
        TextView hitPosition = (TextView) convertView.findViewById(R.id.hitPosition);
        ImageView playByPlayImage = (ImageView) convertView.findViewById(R.id.us_pbp_image);

        if(playByPlayList.get(position).getPlaybyPlayComments().equals("Strike Out"))
        {
            playByPlayImage.setBackgroundResource(R.drawable.red_small_circle);
        }
        else
        {
            playByPlayImage.setBackgroundResource(R.drawable.green_small_circle);

        }



        playNo.setText(playByPlayList.get(position).getPlayNo()+" ");
        hitPosition.setText(playByPlayList.get(position).getPlaybyPlayComments());






        return convertView;
    }
}