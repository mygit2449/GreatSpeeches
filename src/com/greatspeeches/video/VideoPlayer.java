package com.greatspeeches.video;

import com.greatspeeches.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity
{
	private VideoView mVideoView;
	private Intent receiverIntent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    receiverIntent = getIntent();
	    this.setContentView(R.layout.video);
	    CustomVideoView cVideoView = (CustomVideoView) findViewById(R.id.surface_video);
	    cVideoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {

	        @Override
	        public void onPlay() {
	            System.out.println("Play!");
	        }

	        @Override
	        public void onPause() {
	            System.out.println("Pause!");

	        }
	    });

	    DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) cVideoView.getLayoutParams();
	    params.width =  metrics.widthPixels;
	    params.height = metrics.heightPixels;
	    params.leftMargin = 0;
	    cVideoView.setLayoutParams(params);
	    cVideoView.setMediaController(new MediaController(this));
	    cVideoView.setVideoURI(Uri.parse(receiverIntent.getExtras().getString("url")));
	    cVideoView.start();
	    cVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	        public void onCompletion(MediaPlayer mp) {
	            finish(); // finish current activity
	        }
	    });
    
	}


}