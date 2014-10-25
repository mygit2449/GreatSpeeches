package com.greatspeeches.video;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.greatspeeches.helper.GreateSpeechesUtil;

public class YouTubeVideoPlayer extends YouTubePlayerSupportFragment{

	private static Handler parentHandler;
	
	@Override
	public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		init();
		return super.onCreateView(arg0, arg1, arg2);
	}

	public YouTubePlayer activePlayer;

    public static YouTubeVideoPlayer newInstance(String url, Handler handler) {
    	YouTubeVideoPlayer playerYouTubeFrag = new YouTubeVideoPlayer();
    	parentHandler = handler;
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        playerYouTubeFrag.setArguments(bundle);
        return playerYouTubeFrag;
    }
 
    private void init() {
        initialize(GreateSpeechesUtil.yOUTUBEdEVELOPERkEY, new OnInitializedListener() {
            @Override
            public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                activePlayer.setPlayerStateChangeListener(videoListener);
                if (!wasRestored) {
                    activePlayer.loadVideo(getArguments().getString("url"), 0);
                }
            }
        });
    }
	
PlayerStateChangeListener videoListener = new   PlayerStateChangeListener() {
		
		@Override
		public void onVideoStarted() {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onVideoEnded() {
			// TODO Auto-generated method stub
			parentHandler.sendEmptyMessage(2);
		}
		
		@Override
		public void onLoading() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLoaded(String arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onError(ErrorReason arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAdStarted() {
			// TODO Auto-generated method stub
		}
	};
    
    
    
}
