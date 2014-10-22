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

import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greatspeeches.HomeScreen;
import com.greatspeeches.R;
import com.greatspeeches.helper.GreateSpeechesUtil;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.video.CustomVideoView;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 */
public class ScreenSlidePageFragment extends Fragment {

    private HomeDataModel mPersonObj;
    private TextView infoData = null;
    private ImageView personImg = null, closeImg;
    private CustomVideoView cVideoView = null;
    private RelativeLayout videoRel = null;

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

    public ScreenSlidePageFragment() {
    }

        
    public void update(){
    	videoRel.setVisibility(View.VISIBLE);
    	personImg.setVisibility(View.GONE);
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
    }
    
    public void closeVplayer(){
    	if(null != cVideoView){
    		cVideoView.stopPlayback();
    		videoRel.setVisibility(View.GONE);
    		personImg.setVisibility(View.VISIBLE);
    	}
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
        videoRel.setOnTouchListener(new OnTouchListener() {
			
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
		});
        
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
            }
        }
    };



//    /**
//     * Returns the page number represented by this fragment object.
//     */
//    public int getPageNumber() {
//        return mPageNumber;
//    }
}
