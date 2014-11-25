/*

 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.greatspeeches.slides;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.greatspeeches.R;
import com.greatspeeches.helper.GreateSpeechesUtil;
import com.greatspeeches.helper.StickyScrollView;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.video.CustomVideoView;
import com.greatspeeches.video.YouTubeVideoPlayer;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 */
public class ScreenSlidePageFragment extends Fragment{

    private HomeDataModel mPersonObj;
    private TextView infoData = null;
    public ImageView personImg = null, closeImg;
    public CustomVideoView cVideoView = null;
    private RelativeLayout videoRel = null;
    private PersonsDescriptionView myContext;
    
	private FrameLayout fragmentsLayout;

	public YouTubePlayer activePlayer;
	private String videoId =  "";
	private StickyScrollView scroll = null;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(HomeDataModel dataObj) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putParcelable("data_obj", dataObj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(PersonsDescriptionView) activity;
        super.onAttach(activity);
    }
    
    public ScreenSlidePageFragment() {
    }

        
    public void update(){
    	scroll.fullScroll(View.FOCUS_UP);
    	if(mPersonObj.getType().equalsIgnoreCase("Popular") && !mPersonObj.getVideourl().contains("youtube")){
    		personImg.setVisibility(View.GONE);
    		videoRel.setVisibility(View.VISIBLE);
    		cVideoView.setVisibility(View.VISIBLE);
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
    		
    		cVideoView.setMediaController(new MediaController(getActivity()));
    		cVideoView.setVideoURI(Uri.parse( "android.resource://com.greatspeeches/raw/"+mPersonObj.getVideourl()));
    		cVideoView.start();
    		cVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    			public void onCompletion(MediaPlayer mp) {
//	            finish(); // finish current activity
    				closeVplayer();
    			}
    		});
    	}else{
    		playYutubeVideo();
    	}
    }
    
    
    public void playYutubeVideo(){
    	fragmentsLayout.setVisibility(View.VISIBLE);
		videoRel.setVisibility(View.VISIBLE);
		if(mPersonObj.getVideourl().length() > 0){
			int indexPos = mPersonObj.getVideourl().indexOf("=");
			if(indexPos > 0){
				videoId = mPersonObj.getVideourl().substring(indexPos+1, mPersonObj.getVideourl().length());
			}
			 
			android.app.FragmentTransaction fts = getChildFragmentManager().beginTransaction();
			YouTubePlayerFragment youTubePlayerFragment = new YouTubePlayerFragment();
//			YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
//			FragmentTransaction fts = myContext.getSupportFragmentManager().beginTransaction();
			fts.add(R.id.video_container,youTubePlayerFragment);
			fts.addToBackStack(null);
			fts.commit();

			myContext.getSupportFragmentManager().executePendingTransactions();
			
			youTubePlayerFragment.initialize(GreateSpeechesUtil.yOUTUBEdEVELOPERkEY, new OnInitializedListener() {
			    @Override
			    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
			            // Play, cue or whatever you want to do with the player
			    	 activePlayer = player;
		                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
		                activePlayer.setPlayerStateChangeListener(videoListener);
		                activePlayer.setFullscreen(false);
		                activePlayer.setShowFullscreenButton(false);
		                if (!wasRestored) {
		                    activePlayer.loadVideo(videoId, 0);
		                }
			    }
	
				@Override
				public void onInitializationFailure(Provider arg0,
						YouTubeInitializationResult arg1) {
					// TODO Auto-generated method stub
				}
			});
		}
    }
    
    
    public void closeVplayer(){
    	if(null != cVideoView){
    		cVideoView.stopPlayback();
    		videoRel.setVisibility(View.GONE);
    		personImg.setVisibility(View.VISIBLE);
    	}
    }
    
    public void closeYVplayer(){
    	YouTubeVideoPlayer fragB = (YouTubeVideoPlayer) myContext.getSupportFragmentManager().findFragmentByTag("videoFrag");
    	android.support.v4.app.FragmentManager manager =myContext.getSupportFragmentManager();
		if(null != fragB){
			android.support.v4.app.FragmentTransaction trans = manager.beginTransaction();
			trans.remove(fragB);
			trans.commit();
			manager.popBackStack();
		}
		
      	android.support.v4.app.FragmentManager fragmentManager = myContext.getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 0) {
		    fragmentManager.popBackStack();
		}
		videoRel.setVisibility(View.GONE);
		personImg.setVisibility(View.VISIBLE);
		fragmentsLayout.setVisibility(View.GONE);
    }
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPersonObj = (HomeDataModel) getArguments().getParcelable("data_obj");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.description_layout, container, false); 
        
    	final android.support.v4.app.FragmentManager fragmentManager = myContext.getSupportFragmentManager();
    	fragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
        		if (fragmentManager.getBackStackEntryCount() == 0) {
        		    closeYVplayer();
        		}
            }
        });
        
        scroll = (StickyScrollView)rootView.findViewById(R.id.topScroll);
        fragmentsLayout = (FrameLayout)rootView.findViewById(R.id.video_container);
        // Set the title view to show the page number.
        infoData =  ((TextView) rootView.findViewById(R.id.person_info));
        infoData.setMaxLines(Integer.MAX_VALUE);
        infoData.setMovementMethod(new ScrollingMovementMethod());
        infoData.setText("\t\t\t"+mPersonObj.getInfo());
        personImg = (ImageView) rootView.findViewById(R.id.person_image);
        personImg.setBackgroundResource(GreateSpeechesUtil.getResId(mPersonObj.getImageId(), R.drawable.class));
        cVideoView = (CustomVideoView)rootView.findViewById(R.id.surface_video);
        closeImg = (ImageView)rootView.findViewById(R.id.closeBtn);
        videoRel = (RelativeLayout)rootView.findViewById(R.id.forVideo);
        videoRel.setOnTouchListener(customTouchListener);
        fragmentsLayout.setOnTouchListener(customTouchListener);

        if(null != mPersonObj.getdDate() && mPersonObj.getdDate().length() == 0){
        	RelativeLayout dRel = (RelativeLayout)rootView.findViewById(R.id.dDateRel);
        	dRel.setVisibility(View.GONE);
        }
        
        TextView bDateTxt = (TextView)rootView.findViewById(R.id.bDate);
        bDateTxt.setText(""+mPersonObj.getbDate());
        TextView bachievementTxt = (TextView)rootView.findViewById(R.id.C_info);
        bachievementTxt.setText(""+mPersonObj.getAchievement());
//        TextView pNameTxt = (TextView)rootView.findViewById(R.id.acTitleTxt);
//        pNameTxt.setText(""+getResources().getString(R.string.achievement, mPersonObj.getName()));
        TextView dDateTxt = (TextView)rootView.findViewById(R.id.dDate);
        dDateTxt.setText(""+mPersonObj.getdDate());
        
        closeImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeVplayer();
			}
		});
        
        return rootView;
    }
  

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what==1){
		    	closeImg.setVisibility(View.GONE);
            }else if(msg.what==2){
            	closeYVplayer();
            }else if(msg.what==3){
            	personImg.setVisibility(View.GONE);
            }
        }
    };
    
    OnTouchListener customTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
		    case MotionEvent.ACTION_DOWN:
		    	closeImg.setVisibility(View.VISIBLE);
		    	handler.sendEmptyMessageDelayed(1, 2000);
		        break;
		    case MotionEvent.ACTION_MOVE:
		        // do something
		        break;
		    case MotionEvent.ACTION_UP:
		       //do something
		        break;
		}
		return true;
		}
	};
 

	PlayerStateChangeListener videoListener = new   PlayerStateChangeListener() {
		
		@Override
		public void onVideoStarted() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(3);
		}
		
		@Override
		public void onVideoEnded() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(2);
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
