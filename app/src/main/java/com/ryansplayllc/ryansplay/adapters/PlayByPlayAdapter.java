package com.ryansplayllc.ryansplay.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by nimaikrsna on 5/22/2015.
 */
public class PlayByPlayAdapter extends BaseAdapter
{

    List<Player> players;
    Activity activity;
    LayoutInflater inflater;
    ArrayList<PlayByPlay> playByPlayList;
    private String playByPlayPoints;

    public PlayByPlayAdapter(Activity activity, ArrayList<PlayByPlay> playByPlayList ,String playByPlayPoints) {
        this.activity = activity;
        Rollbar.setIncludeLogcat(true);

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.playByPlayList=playByPlayList;
        this.playByPlayPoints = playByPlayPoints;
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
                R.layout.play_by_play_list_item, parent, false);
        TextView playNo= (TextView) convertView.findViewById(R.id.playNo);
        TextView hitPosition = (TextView) convertView.findViewById(R.id.hitPosition);
        TextView playByPlayPointsTextView = (TextView) convertView.findViewById(R.id.playbyplay_game_points);
        ImageView playByPlayImage = (ImageView) convertView.findViewById(R.id.gs_pbp_image);

        if(playByPlayList.get(position).getPlaybyPlayComments().equals("Strike Out"))
        {
            playByPlayImage.setBackgroundResource(R.drawable.red_small_circle);
        }
        else
        {
            playByPlayImage.setBackgroundResource(R.drawable.green_small_circle);

        }


        //converting the points string to array list
        ArrayList<String> pBPHistoryPointsList=new ArrayList(Arrays.asList(playByPlayPoints.split(",")));

        //reversing the arraylist
        Collections.reverse(pBPHistoryPointsList);

        playNo.setText(playByPlayList.get(position).getPlayNo()+" ");
        hitPosition.setText(playByPlayList.get(position).getPlaybyPlayComments());
        try{
            playByPlayPointsTextView.setText(pBPHistoryPointsList.get(position)+"");
        }catch(Exception e){
            Log.e("**inside try err",e.getMessage());
        }

        return convertView;
    }
}
