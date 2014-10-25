package com.greatspeeches.slides;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphObject;
import com.greatspeeches.R;
import com.greatspeeches.helper.ArcMenu;
import com.greatspeeches.helper.AudioPlayer;
import com.greatspeeches.helper.GreateSpeechesUtil;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.socialhub.TwitActivity;
import com.greatspeeches.util.ConnectionDetector;

public class PersonsDescriptionView extends FragmentActivity implements OnClickListener, OnCompletionListener{
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;
    private Intent receiverIntent = null;
    private int selectPos = 0;
	private static final int[] ITEM_DRAWABLES = { R.drawable.facebook, R.drawable.headphone,R.drawable.video_icon,R.drawable.twitter};

	private Button _playBtn, _prevBtn, _nextBtn;
	private ImageView _closeBtn, _personImage;
	private Animation topToBottomanim, bottomToTopAnim;   
	private RelativeLayout _playerLayout = null;
	private ArcMenu arcMenu2;
	private AudioPlayer _customPlayer = null;
	private boolean isPlaying = false;
	
	private PendingAction pendingAction = PendingAction.NONE;
    private static final String PERMISSION = "publish_actions";
    private Session session = null;
    private ConnectionDetector network = null;
    public  ArrayList<HomeDataModel> dataList = null;

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {

        if (pendingAction != PendingAction.NONE && (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
            pendingAction = PendingAction.NONE;
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
	        performPublish(PendingAction.POST_PHOTO, false);
        }else if (state == SessionState.OPENED) {
	        performPublish(PendingAction.POST_PHOTO, false);
        }
    }
    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_persons_description_view); 
		
		network = new ConnectionDetector(PersonsDescriptionView.this);
		
		dataList = getIntent().getParcelableArrayListExtra("popularItems");
		
		initializeUI();
		
		_customPlayer = new AudioPlayer();
		_customPlayer.setOnCompletionListener(this);
		
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		
		 Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

	        Session session = Session.getActiveSession();
	        if (session == null) {
	            if (savedInstanceState != null) {
	                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
	            }
	            if (session == null) {
	                session = new Session(this);
	            }
	            Session.setActiveSession(session);
	            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
	                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
	            }
	        }
		
	}

	public void initializeUI(){
		
		_playBtn  = (Button)findViewById(R.id.play_pause_btn);
		_prevBtn  = (Button)findViewById(R.id.prev_btn);
		_nextBtn  = (Button)findViewById(R.id.next_btn);
		_closeBtn = (ImageView)findViewById(R.id.close_audio);
		_personImage = (ImageView)findViewById(R.id.person_img);
		_playerLayout = (RelativeLayout)findViewById(R.id.audio_rel1);
		
		_playBtn.setOnClickListener(this);
		_prevBtn.setOnClickListener(this);
		_nextBtn.setOnClickListener(this);
		_closeBtn.setOnClickListener(this);
		arcMenu2 = (ArcMenu) findViewById(R.id.arc_menu_2); 
		initArcMenu(arcMenu2, ITEM_DRAWABLES);
		
		receiverIntent = getIntent();
		selectPos = receiverIntent.getExtras().getInt("position");
		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(selectPos);
		mPager.setOffscreenPageLimit(1);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(""+dataList.get(selectPos).getName());
		_personImage.setBackgroundResource(GreateSpeechesUtil.getResId(dataList.get(selectPos).getImageId(), R.drawable.class));
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				selectPos = position;
				// When changing pages, reset the action bar actions since they are dependent
				// on which page is currently active. An alternative approach is to have each
				// fragment expose actions itself (rather than the activity exposing actions),
				// but for simplicity, the activity provides the actions in this sample.
				getActionBar().setTitle(""+dataList.get(position).getName());
				_personImage.setBackgroundResource(GreateSpeechesUtil.getResId(dataList.get(position).getImageId(), R.drawable.class));
				invalidateOptionsMenu();
//				mPagerAdapter.getFragment(position).closeVplayer();
//				mPagerAdapter.getFragment(position).closeYVplayer();

				if (_customPlayer.isPlaying()) {
					_playBtn.setBackgroundResource(R.drawable.play_button_status);
					resetAudioPlayer();
					Thread _playerThread = new Thread(_buttonUpdate);
					_playerThread.start();
				}else{
					_playerLayout.setVisibility(View.GONE);
					resetAudioPlayer();
				}
				mPagerAdapter.notifyDataSetChanged();
			}
		});
		
		
		topToBottomanim = AnimationUtils.loadAnimation(PersonsDescriptionView.this, R.anim.slide_top_to_bottom);
		bottomToTopAnim = AnimationUtils.loadAnimation(PersonsDescriptionView.this, R.anim.slide_bottom_to_top);
		
	}
	
	Runnable _buttonUpdate = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				runOnUiThread(_changeStatus);
			}
		};
	
   Runnable _changeStatus = new Runnable() {
	@Override
	public void run() {
		// TODO Auto-generated method stub
		play(dataList.get(selectPos).getAudio());
	}
   };
	
   @Override
   public void onStart() {
       super.onStart();
       Session.getActiveSession().addCallback(statusCallback);
   }

   @Override
   public void onStop() {
       super.onStop();
       Session.getActiveSession().removeCallback(statusCallback);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
   }

   @Override
   protected void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
       Session session = Session.getActiveSession();
       Session.saveSession(session, outState);
   }
   private void resetAudioPlayer(){
	    _customPlayer.stop();
		_customPlayer.reset();
		_customPlayer = new AudioPlayer();
		_customPlayer.setOnCompletionListener(PersonsDescriptionView.this);
   }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.persons_description_view, menu);
		menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);
        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,(mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)  ? R.string.action_finish : 
        				R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	                // Navigate "up" the demo structure to the launchpad activity.
	                // See http://developer.android.com/design/patterns/navigation.html for more.
//	            	getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//	                startActivity(new Intent(PersonsDescriptionView.this, HomeScreen.class).setAction(getIntent().getAction()));
	                finish();
	                return true;

	            case R.id.action_previous:
	                // Go to the previous step in the wizard. If there is no previous step,
	                // setCurrentItem will do nothing.
	                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
	                return true;

	            case R.id.action_next:
	                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
	                // will do nothing.
	                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
	                return true;
	        }

	        return super.onOptionsItemSelected(item);
	    }
	
	 /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		private SparseArray<WeakReference<ScreenSlidePageFragment>> mPageReferenceMap = new SparseArray<WeakReference<ScreenSlidePageFragment>>();
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
		public Fragment getItem(int index) {
			return getFragment(index);
		}
//        @Override
//        public Fragment getItem(int position) {
//            return ScreenSlidePageFragment.create(HomeScreen.homeDataarr.get(position));
//        }

        @Override
        public int getItemPosition(Object object){
            return ScreenSlidePagerAdapter.POSITION_NONE;
        }
        @Override
        public int getCount() {
            return dataList.size();
        }
        
        public Object instantiateItem(ViewGroup container, int position) {

        	ScreenSlidePageFragment screenFragment = ScreenSlidePageFragment.create(dataList.get(position));
			mPageReferenceMap.put(Integer.valueOf(position), new WeakReference<ScreenSlidePageFragment>(screenFragment));

			return super.instantiateItem(container, position);
		}
        
        public ScreenSlidePageFragment getFragment(int key) {
			WeakReference<ScreenSlidePageFragment> weakReference = mPageReferenceMap.get(key);
			if (null != weakReference) {
				return (ScreenSlidePageFragment) weakReference.get();
			}
			else {
				return null;
			}
		}
    }
    
    com.greatspeeches.socialhub.SocialMessageListener _receiveMessageListener = new com.greatspeeches.socialhub.SocialMessageListener() {
		
		@Override
		public void onError(String message) {
			// TODO Auto-generated method stub
			Toast.makeText(PersonsDescriptionView.this, " "+message, Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onComplete(String message) {
			// TODO Auto-generated method stub
			Toast.makeText(PersonsDescriptionView.this, " "+message, Toast.LENGTH_SHORT).show();
		}
	};
	
	 private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
	        final int itemCount = itemDrawables.length;
	        for (int i = 0; i < itemCount; i++) {
	        	
	        	
	        	
	        	if(itemDrawables[i] == R.drawable.headphone && (null == dataList.get(selectPos).audio || dataList.get(selectPos).audio.length() == 0)){
	        		continue ;
	        	}
	        	
	        		ImageView item = new ImageView(this);
	        		item.setImageResource(itemDrawables[i]);
	        		
	        		final int position = i;
		            menu.addItem(item, new OnClickListener() {

		                @Override
		                public void onClick(View v) {
		                	if(ITEM_DRAWABLES[position] == R.drawable.facebook){
		                		
		                		 session = Session.getActiveSession();
		                			if (!session.isOpened() && !session.isClosed()) { 
		                				session.openForRead(new Session.OpenRequest(PersonsDescriptionView.this).setCallback(statusCallback));
		                			} else {
		                				Session.openActiveSession(PersonsDescriptionView.this, true, statusCallback);
		                			}
		                			
		                	}else if(ITEM_DRAWABLES[position] == R.drawable.headphone){
		                		
		                		_playerLayout.startAnimation(bottomToTopAnim);
		                		bottomToTopAnim.setAnimationListener(new AnimationListener() {
									
									@Override
									public void onAnimationStart(Animation animation) {
										// TODO Auto-generated method stub
										
									}
									
									@Override
									public void onAnimationRepeat(Animation animation) {
										// TODO Auto-generated method stub
										
									}
									
									@Override
									public void onAnimationEnd(Animation animation) {
										// TODO Auto-generated method stub
										_playerLayout.setVisibility(View.VISIBLE);
										play(dataList.get(selectPos).audio);
									}
								});
		                		
		                	}else if(ITEM_DRAWABLES[position] == R.drawable.twitter){
		                		if(network.isConnectingToInternet()){
		                			startActivity(new Intent(PersonsDescriptionView.this, TwitActivity.class).putExtra("img_name", dataList.get(selectPos).getImageId()).putExtra("quote", dataList.get(selectPos).getQuote()));
		                		}else{
		                			Toast.makeText(PersonsDescriptionView.this, "oops!,Please check Internet connection and try again!", Toast.LENGTH_SHORT).show();
		                		}
		                	}else if(ITEM_DRAWABLES[position] == R.drawable.video_icon){
		                		ScreenSlidePageFragment sFragment = mPagerAdapter.getFragment(selectPos);
		        				sFragment.update();
		                	}
					    }
		            });
	        	}
	    }

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	 
		switch (v.getId()) {
		case R.id.play_pause_btn:
			play(dataList.get(selectPos).audio);
			break;
		case R.id.next_btn:
			mPager.setCurrentItem(selectPos+1);
			break;
		case R.id.prev_btn:
			mPager.setCurrentItem(selectPos-1);
			break;
		case R.id.close_audio:
			stopAudioAnimation();
			break;
		default:
			break;
		}
	}
	
	
	private void stopAudioAnimation(){
		_playerLayout.startAnimation(topToBottomanim);
		topToBottomanim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				_playerLayout.setVisibility(View.GONE);
			}
		});
	}
	
	
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		_playBtn.setBackgroundResource(R.drawable.play_button_img);
	}
	
	private void play(final String fileName){
		AssetFileDescriptor afd = null;
		try {
			afd = getAssets().openFd(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
	    	_customPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
	    	_customPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    if (_customPlayer != null && _customPlayer.isPlaying()) {
			_customPlayer.pause();
			_playBtn.setBackgroundResource(R.drawable.play_button_status);
		}else{
			
			int length =  _customPlayer.getCurrentPosition();
			
			if (length == 0) {
				_customPlayer.start();
			}else{
				_customPlayer.seekTo(length);
				_customPlayer.start();
			}
			_playBtn.setBackgroundResource(R.drawable.pause_button_status);
		}
	}

	@Override
	 public void onDestroy(){
	 super.onDestroy();
	    _customPlayer.release();
	 }
	
	 private void performPublish(PendingAction action, boolean allowNoSession) {
	        if (session != null) {
	            pendingAction = action;
	            if (hasPublishPermission()) {
	                // We can do the action right away.
	                handlePendingAction();
	                return;
	            } else if (session.isOpened()) {
	                // We need to get new permissions, then complete the action when we get called back.
	                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
	                return;
	            }
	        }

	        if (allowNoSession) {
	            pendingAction = action;
	            handlePendingAction();
	        }
	    }
	 
	 @SuppressWarnings("incomplete-switch")
	    private void handlePendingAction() {
	        PendingAction previouslyPendingAction = pendingAction;
	        // These actions may re-set pendingAction if they are still pending, but we assume they
	        // will succeed.
	        pendingAction = PendingAction.NONE;

	        switch (previouslyPendingAction) {
	            case POST_PHOTO:
	                postPhoto();
	                break;
	            case POST_STATUS_UPDATE:
//	                postStatusUpdate();
	                break;
	        }
	    }
	 
	 private boolean hasPublishPermission() {
	        Session session = Session.getActiveSession();
	        return session != null && session.getPermissions().contains("publish_actions");
	    }
	 
	 private void postPhoto() {
	        if (hasPublishPermission()) {
	            Bitmap image = BitmapFactory.decodeResource(this.getResources(), GreateSpeechesUtil.getResId(dataList.get(selectPos).getImageId(), R.drawable.class));
	            Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), image, new Request.Callback() {
	                @Override
	                public void onCompleted(Response response) {
	                    showPublishResult(getString(R.string.photo_post), response.getGraphObject(), response.getError());
	                }
	            });
	            request.executeAsync();
	        } else {
	            pendingAction = PendingAction.POST_PHOTO;
	        }
	    }
	 private interface GraphObjectWithId extends GraphObject {
	        String getId();
	    }
	 private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
	        String title = null;
	        String alertMessage = null;
	        if (error == null) {
	            title = getString(R.string.success);
	            String id = result.cast(GraphObjectWithId.class).getId();
	            alertMessage = getString(R.string.successfully_posted_post, message, id);
	        } else {
	            title = getString(R.string.error);
	            alertMessage = error.getErrorMessage();
	        }
	        try {
	        	new AlertDialog.Builder(PersonsDescriptionView.this)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
			} catch (Exception e) {
				// TODO: handle exception
			}
	        
	    }
	 
	 
}
