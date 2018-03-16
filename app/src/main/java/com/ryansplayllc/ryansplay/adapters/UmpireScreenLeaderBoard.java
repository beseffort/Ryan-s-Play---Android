package com.ryansplayllc.ryansplay.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.R;
import com.ryansplayllc.ryansplay.models.Player;

import java.util.List;

/**
 * Created by nimaikrsna on 5/13/2015.
 */
public class UmpireScreenLeaderBoard extends BaseAdapter {

    List<Player> players;
    Activity activity;
    LayoutInflater inflater;
    public UmpireScreenLeaderBoard(Activity activity) {
        this.activity = activity;
        Rollbar.setIncludeLogcat(true);

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 20;
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
                R.layout.umpire_screen_leaderboard, parent, false);
        return convertView;
    }
}
