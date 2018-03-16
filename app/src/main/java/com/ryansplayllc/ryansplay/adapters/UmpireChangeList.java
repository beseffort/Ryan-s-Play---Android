package com.ryansplayllc.ryansplay.adapters;

import java.util.List;

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
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.R;
import com.ryansplayllc.ryansplay.models.Umpire;

public class UmpireChangeList extends BaseAdapter {

	List<Umpire> umpires;
	Activity activity;
	LayoutInflater inflater;
	Umpire umpire;
    String currentUmpireUserName;

	public UmpireChangeList(List<Umpire> umpires, Activity activity , String currentUmpireUserName) {

		// roll bar
		Rollbar.setIncludeLogcat(true);

		this.umpires = umpires;
		this.activity = activity;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.currentUmpireUserName = currentUmpireUserName;
	}

	@Override
	public int getCount() {
		return umpires.size();
	}

	@Override
	public Object getItem(int position) {
		return umpires.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.game_lobby_player_list_item, parent, false);
			ImageView profilePicImageView = (ImageView) convertView
					.findViewById(R.id.pl_iv_profile_pic);
			TextView nameTextView = (TextView) convertView
					.findViewById(R.id.pl_tv_player_name);
			TextView noOfPlaysTextView = (TextView) convertView
					.findViewById(R.id.pl_tv_no_of_plays);

            ImageView umpireMaskImage=(ImageView) convertView.findViewById(R.id.umpiremask);

            umpireMaskImage.setVisibility(View.GONE);

            noOfPlaysTextView.setVisibility(View.GONE);
			umpire = (Umpire) getItem(position);

            Log.e("change umpire list umpire uusername",this.currentUmpireUserName);

            if(umpire.getUserName().equals(this.currentUmpireUserName))
            {
                umpireMaskImage.setVisibility(View.VISIBLE);
            }
            else
            {
                umpireMaskImage.setVisibility(View.GONE);
            }


            if (umpire.getRoundedUmpireImage() != null) {
				profilePicImageView.setImageBitmap(umpire.getRoundedUmpireImage());
			} else {
				ImageLoader imageLoader = new ImageLoader();
				imageLoader.imageView = profilePicImageView;
				imageLoader.umpire = umpire;
				imageLoader.run();
			}
			nameTextView.setText(umpire.getUserName());
			noOfPlaysTextView.setText(Integer.toString(umpire.getNoOfPlays()));
		}
		return convertView;
	}

	private class ImageLoader implements Runnable {
		public Umpire umpire;
		public ImageView imageView;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			RequestQueue queue = Volley.newRequestQueue(activity
					.getApplicationContext());
			ImageRequest imageRequest = new ImageRequest(umpire.getUmpireURL(),
					new Listener<Bitmap>() {

						@Override
						public void onResponse(Bitmap bitmap) {
							umpire.setUmpireImage(bitmap);
							imageView.setImageBitmap(umpire.getRoundedUmpireImage());
						}
					}, 0, 0, null, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// TODO Auto-generated method stub
							Log.d("image loader error", volleyError.toString());
						}
					});
			queue.add(imageRequest);
			queue.start();
		}
	}

}
