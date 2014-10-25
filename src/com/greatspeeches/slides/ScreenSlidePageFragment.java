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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.greatspeeches.HomeScreen;
import com.greatspeeches.R;
import com.greatspeeches.helper.GreateSpeechesUtil;
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
    private ImageView personImg = null, closeImg;
    private CustomVideoView cVideoView = null;
    private RelativeLayout videoRel = null;
    private FragmentActivity myContext;
    
	private FrameLayout fragmentsLayout;

	private YouTubeVideoPlayer myFragment = null;
	
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
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }
    
    public ScreenSlidePageFragment() {
    }

        
    public void update(){

    	videoRel.setVisibility(View.VISIBLE);
    	personImg.setVisibility(View.GONE);
    	
    	if(mPersonObj.getType().equalsIgnoreCase("Popular")){
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
    	
		String videoId =  "";
		int indexPos = mPersonObj.getVideourl().indexOf("=");
		if(indexPos > 0){
			videoId = mPersonObj.getVideourl().substring(indexPos+1, mPersonObj.getVideourl().length());
		}
		
    	personImg.setVisibility(View.GONE);
    	fragmentsLayout.setVisibility(View.VISIBLE);
		myFragment = YouTubeVideoPlayer.newInstance(videoId, handler);
		FragmentTransaction fts = myContext.getSupportFragmentManager().beginTransaction();
		// Replace the content of the container
		fts.replace(R.id.video_container, myFragment,"videoFrag"); 
		
		// Append this transaction to the backstack
		fts.addToBackStack("video");
		// Commit the changes
		fts.commit();
		
    }
    
    
    public void closeVplayer(){
    	if(null != cVideoView){
    		cVideoView.stopPlayback();
    		videoRel.setVisibility(View.GONE);
    		personImg.setVisibility(View.VISIBLE);
    	}
    }
    
    public void closeYVplayer(){
      	FragmentManager fragmentManager = myContext.getSupportFragmentManager();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.description_layout, container, false); 
        
    	final FragmentManager fragmentManager = myContext.getSupportFragmentManager();
    	fragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
        		if (fragmentManager.getBackStackEntryCount() == 0) {
        		    closeYVplayer();
        		}
            }
        });
        
        
        fragmentsLayout = (FrameLayout)rootView.findViewById(R.id.video_container);
        // Set the title view to show the page number.
        infoData =  ((TextView) rootView.findViewById(R.id.person_info));
        infoData.setMaxLines(Integer.MAX_VALUE);
        infoData.setTypeface(HomeScreen.arimoype);
        infoData.setMovementMethod(new ScrollingMovementMethod());
        infoData.setText("\t\t\t"+mPersonObj.getInfo());
        personImg = (ImageView) rootView.findViewById(R.id.person_image);
        personImg.setBackgroundResource(GreateSpeechesUtil.getResId(mPersonObj.getImageId(), R.drawable.class));
        cVideoView = (CustomVideoView)rootView.findViewById(R.id.surface_video);
        closeImg = (ImageView)rootView.findViewById(R.id.closeBtn);
        videoRel = (RelativeLayout)rootView.findViewById(R.id.forVideo);
        videoRel.setOnTouchListener(customTouchListener);
        fragmentsLayout.setOnTouchListener(customTouchListener);

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
    
    
}
