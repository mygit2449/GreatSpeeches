package com.greatspeeches.slides;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.greatspeeches.MainActivity;
import com.greatspeeches.R;
import com.greatspeeches.database.GreatLivesDataBaseHelper;
import com.greatspeeches.helper.ArcMenu;
import com.greatspeeches.helper.AudioDownloaderTask;
import com.greatspeeches.helper.AudioPlayer;
import com.greatspeeches.helper.GreateSpeechesUtil;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.models.Quotes;
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
	private RelativeLayout _playerLayout = null, hintRel = null;
	public ArcMenu arcMenu2;
	private AudioPlayer _customPlayer = null;
	
	private PendingAction pendingAction = PendingAction.NONE;
    private static final String PERMISSION = "publish_actions";
    private Session session = null;
    private ConnectionDetector network = null;
    public  ArrayList<HomeDataModel> dataList = null;
    private ActionBar actionBar;
    private ScreenSlidePageFragment selecedFragment = null;
	private Animation topToBottomanimForicon, bottomToTopAnimForicon;
	private SharedPreferences appPrefs = null;
	private boolean isFirstTime = true;
	private GreatLivesDataBaseHelper mDataBaseHelper = null;
	private  TextView personText;
	public  String LOG_TAG = PersonsDescriptionView.this.getClass().getName();
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
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_persons_description_view); 
		
		appPrefs = this.getSharedPreferences("gl", MODE_PRIVATE);
		isFirstTime  = appPrefs.getBoolean("firstTime", true);
		mDataBaseHelper = new GreatLivesDataBaseHelper(PersonsDescriptionView.this);
		mDataBaseHelper.openDataBase();
		
		network = new ConnectionDetector(PersonsDescriptionView.this);
		
		dataList = getIntent().getParcelableArrayListExtra("popularItems");
		
		initializeUI();
		
		_customPlayer = new AudioPlayer();
		_customPlayer.setOnCompletionListener(this);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		
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
		
	        
	        
	        final int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
	        View v = findViewById(abTitleId);
	        v.setPadding(10, 20, 2, 20);
	        v.setBackgroundResource(R.drawable.background_tab);
	        v.setOnClickListener(new View.OnClickListener() {
	            @Override 
	            public void onClick(View v) {
	            	touchHandle.sendEmptyMessage(0);
	            	designQuoteDialog(""+dataList.get(selectPos).quote);
	            } 
	        }); 
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
		
		receiverIntent = getIntent();
		selectPos = receiverIntent.getExtras().getInt("position");

		hintRel = (RelativeLayout)findViewById(R.id.hintRel);
		personText = (TextView)findViewById(R.id.hint);
		personText.setText(this.getResources().getString(R.string.demo_hint,dataList.get(selectPos).getName()));
		initArcMenu(arcMenu2, ITEM_DRAWABLES);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(selectPos);
		mPager.setOffscreenPageLimit(1);
		selecedFragment = mPagerAdapter.getFragment(selectPos);
		
		if(isFirstTime) {
			hintRel.setVisibility(View.VISIBLE);
		}else{
			hintRel.setVisibility(View.GONE);
		}
		
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(""+dataList.get(selectPos).getName());
		actionBar.setCustomView(R.layout.action_title);
//		actionBar.setDisplayShowTitleEnabled(false);
//		View v = actionBar.getCustomView();
	
		
		
//		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
//		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));
		_personImage.setBackgroundResource(GreateSpeechesUtil.getResId(dataList.get(selectPos).getImageId(), R.drawable.class));
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				selectPos = position;
				// When changing pages, reset the action bar actions since they are dependent
				// on which page is currently active. An alternative approach is to have each
				// fragment expose actions itself (rather than the activity exposing actions),
				// but for simplicity, the activity provides the actions in this sample.
				actionBar.setTitle(""+dataList.get(selectPos).getName());
				_personImage.setBackgroundResource(GreateSpeechesUtil.getResId(dataList.get(position).getImageId(), R.drawable.class));
				invalidateOptionsMenu();
//				mPagerAdapter.getFragment(position).closeVplayer();
//				mPagerAdapter.getFragment(position).setHandler(touchHandle);
				
				if(dataList.get(position).getType().equalsIgnoreCase("Popular")){
					initArcMenu(arcMenu2, ITEM_DRAWABLES);
				}

				if(arcMenu2.mArcLayout.isExpanded()){
					arcMenu2.mHintView.startAnimation(arcMenu2.createHintSwitchAnimation(true));
					arcMenu2.mArcLayout.switchState(false);
				}else{
				}
				
				if (_playerLayout.getVisibility() == View.VISIBLE) {
					resetAudioPlayer();
					stopAudioAnimation();
				}else{
//					_playerLayout.setVisibility(View.GONE);
//					resetAudioPlayer();
				}
				mPagerAdapter.notifyDataSetChanged();
			}
		});
		
		
		topToBottomanim = AnimationUtils.loadAnimation(PersonsDescriptionView.this, R.anim.slide_top_to_bottom);
		bottomToTopAnim = AnimationUtils.loadAnimation(PersonsDescriptionView.this, R.anim.slide_bottom_to_top);
		
		
		topToBottomanimForicon = AnimationUtils.loadAnimation(PersonsDescriptionView.this, R.anim.slide_top_to_bottom_icon);
		bottomToTopAnimForicon = AnimationUtils.loadAnimation(PersonsDescriptionView.this, R.anim.slide_bottom_to_top_icon);
		
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
   public void onResume(){
	   super.onResume();
	   Session session = Session.getActiveSession();
	   session.addCallback(statusCallback);
   }
   @Override 
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       Log.i(LOG_TAG, "result..."+requestCode+"..resultCode.."+resultCode);
       Session.getActiveSession().onActivityResult(PersonsDescriptionView.this, requestCode, resultCode, data);
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
		menu.findItem(R.id.action_previous).setVisible(mPager.getCurrentItem() > 0);
		menu.findItem(R.id.action_next).setVisible(mPager.getCurrentItem() < mPagerAdapter.getCount() - 1);
		
		if(dataList.get(selectPos).getType().equalsIgnoreCase("Popular")){
			menu.findItem(R.id.action_home).setVisible(false);
		}
		
		
        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
//        MenuItem item = menu.add(Menu.NONE, R.id.action_next);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
	            	
	            	if(dataList.get(selectPos).getType().equalsIgnoreCase("Popular")){
	            		startActivity(new Intent(PersonsDescriptionView.this, MainActivity.class).setAction(getIntent().getAction()));
	            		finish();
	            	}else{
	            		finish();
	            	}
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
	            case R.id.action_home:
	                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
	                // will do nothing.
	            	getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	                startActivity(new Intent(PersonsDescriptionView.this, MainActivity.class).setAction(getIntent().getAction()));
//	                finish();
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
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int getItemPosition(Object object){
            return ScreenSlidePagerAdapter.POSITION_NONE;
        }
        @Override
        public int getCount() {
            return dataList.size();
        }
        
        @Override
        public boolean isViewFromObject(View view, Object object) {
            if(object != null){
                return ((Fragment)object).getView() == view;
            }else{
                return false;
            }
        }
        
        @Override 
        public void destroyItem(ViewGroup container, int position, Object object) {
           mPageReferenceMap.remove(Integer.valueOf(position));
           super.destroyItem(container, position, object);
        } 
        
        
        public Object instantiateItem(ViewGroup container, int position) {
        	ScreenSlidePageFragment screenFragment = ScreenSlidePageFragment.create(dataList.get(position),touchHandle);
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
	        menu.mArcLayout.removeAllViews();	
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
		                		if(null  != selecedFragment && ((null != selecedFragment.cVideoView && selecedFragment.cVideoView.isPlaying()) || (null != selecedFragment.activePlayer && selecedFragment.activePlayer.isPlaying()))){
		                			Toast.makeText(PersonsDescriptionView.this, "Not possible to play audio now, video is already playing.", Toast.LENGTH_SHORT).show();
		                		}else{
		                			if (!isFileExisted(dataList.get(selectPos).audio)) {
		                				if(network.isConnectingToInternet()){
		                					new AudioDownloaderTask(PersonsDescriptionView.this, GreateSpeechesUtil.AUDIO_LINK+dataList.get(selectPos).audio, touchHandle).execute();
				                		}else{
				                			Toast.makeText(PersonsDescriptionView.this, "oops!,Please check Internet connection and try again!", Toast.LENGTH_SHORT).show();
				                		}
		                			}else{
		                				touchHandle.sendEmptyMessage(2);
		                			}
		                		}
		                	}else if(ITEM_DRAWABLES[position] == R.drawable.twitter){
		                		if(network.isConnectingToInternet()){
		                			startActivity(new Intent(PersonsDescriptionView.this, TwitActivity.class).putExtra("img_name", dataList.get(selectPos).getImageId()).putExtra("quote", dataList.get(selectPos).getQuote()));
		                		}else{
		                			Toast.makeText(PersonsDescriptionView.this, "oops!,Please check Internet connection and try again!", Toast.LENGTH_SHORT).show();
		                		}
		                	}else if(ITEM_DRAWABLES[position] == R.drawable.video_icon){
//		                		arcMenu2.setVisibility(View.GONE);
		                		selecedFragment = mPagerAdapter.getFragment(selectPos);
		                		selecedFragment.update();
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
			resetAudioPlayer();
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
		 try {
		    	_customPlayer.setDataSource(fileName);
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
	 
	  
	 Dialog dialog;
	 int selectedQoutePos = 0;
	 public void designQuoteDialog(String quote){
		  dialog = new Dialog(PersonsDescriptionView.this);
		  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		  dialog.setContentView(R.layout.quote_layout);
		  dialog.setCanceledOnTouchOutside(true);
		  final TextView quoteCountTxt = (TextView)dialog.findViewById(R.id.countTxt);
		  final ArrayList<Quotes> quoteList =mDataBaseHelper.getQuotes(dataList.get(selectPos).getId());
		  quoteCountTxt.setText("1"+"/"+quoteList.size());
		  final ViewPager quotePager = (ViewPager)dialog.findViewById(R.id.quote_viewpager);
		  quotePager.setAdapter(new PagerAdapter() {

	            @Override
	            public Object instantiateItem(final ViewGroup collection, final int position) {
	               
	               int resId =  R.layout.onlyquote;
	               LayoutInflater inflater = (LayoutInflater)PersonsDescriptionView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	               View view = inflater.inflate(resId, null);

	      		   TextView quoteTxt = (TextView)view.findViewById(R.id.quoteTxt);
	      		   quoteTxt.setText('"' + quoteList.get(position).getqMessage().trim() + '"');
	      		   quoteTxt.setMovementMethod(new ScrollingMovementMethod());
	               collection.addView(view);
	               
	               return view;
	            }


	            @Override
	            public int getCount() {
	                return quoteList.size();
	            }

	            @Override
	            public boolean isViewFromObject(View view, Object object) {
	                return view == object;
	            }

	            @Override
	            public void destroyItem(ViewGroup container, int position, Object object) {
	                container.removeView((View)object);
	            }

	        });
		  
		  
		  quotePager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				quoteCountTxt.setText(arg0+1+"/"+quoteList.size());
				selectedQoutePos = arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		  
		  ImageView personImage = (ImageView)dialog.findViewById(R.id.popular_img);
		  personImage.setBackgroundResource(GreateSpeechesUtil.getResId(dataList.get(selectPos).getImageId()+"_l", R.drawable.class));
		  final TextView nameTxt = (TextView)dialog.findViewById(R.id.nameTxt);
		  nameTxt.setText(""+dataList.get(selectPos).getName());
		  Button mCloseBtn= (Button)dialog.findViewById(R.id.closeBtn);
		  mCloseBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		  
		  Button mShareBtn= (Button)dialog.findViewById(R.id.shareBtn);
		  mShareBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StringBuilder sb = new StringBuilder();
				sb.append(""+nameTxt.getText().toString()+"\r\n  ");
				sb.append(""+""+quoteList.get(selectedQoutePos).getqMessage().trim());
				
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
				sendIntent.setType("text/plain");
				startActivity(sendIntent);
				
			}
		});
		  
		  
		  dialog.findViewById(R.id.leftArrow).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				quotePager.setCurrentItem(quotePager.getCurrentItem()-1);
			}
		});
		  
		  
		  dialog.findViewById(R.id.rightArrow).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					quotePager.setCurrentItem(quotePager.getCurrentItem()+1);
				}
			});
		  
		  dialog.show();
	 }	 
	 
	 
	 
	 Handler touchHandle = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				if (msg.what==0) {
					
					if(isFirstTime){
						SharedPreferences.Editor preferencesEdit;
						preferencesEdit = appPrefs.edit();
						preferencesEdit.putBoolean("firstTime", !isFirstTime);
						preferencesEdit.commit();
						selecedFragment = mPagerAdapter.getFragment(selectPos);
						hintRel.setVisibility(View.GONE);
						return;
					}
					
					arcMenu2.startAnimation(topToBottomanimForicon);
					topToBottomanimForicon.setAnimationListener(new AnimationListener() {
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
							arcMenu2.setVisibility(View.GONE);
						}
					});
					
					
				}else if (msg.what==2){
										
					_playerLayout.invalidate();
					_playerLayout.setVisibility(View.INVISIBLE);
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
        					int indexPos = dataList.get(selectPos).audio.lastIndexOf("/");
        					String fileName = dataList.get(selectPos).audio.substring(indexPos+1, dataList.get(selectPos).audio.length());
        					String sdcard_path = String.format(Environment.getExternalStorageDirectory().getAbsolutePath()+"/GreatLives"+"/"+fileName);
        					play(sdcard_path);
        				}
        			});
				}else if(msg.what==4){
        			Toast.makeText(PersonsDescriptionView.this, "oops!,A problem occured while playing audio, please try again!", Toast.LENGTH_SHORT).show();
				}else{
					if(isFirstTime){
						isFirstTime = !isFirstTime;
						return;
					}
						
					arcMenu2.startAnimation(bottomToTopAnimForicon);
					bottomToTopAnimForicon.setAnimationListener(new AnimationListener() {
        				
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
        					arcMenu2.setVisibility(View.VISIBLE);
        				}
        			});
					
					
				}
			}
	    	
	    };
	 
	    /**
		 * Checking whether the file existed in SD card or not
		 * @param fileName
		 * @return  true if file existed and false if file not existed.
		 */
		 private boolean  isFileExisted(String fileName)
		    {
			    boolean iChecker = false;

		    	File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/GreatLives");
		    	try
		    	{
		    		boolean recursive = true;
		    		Collection files = FileUtils.listFiles(root, null, recursive);
		    		for (Iterator iterator = files.iterator(); iterator.hasNext();)
		    		{
		    			File file = (File) iterator.next();
		    			if (file.getName().startsWith(fileName))
		    			{
		    				File isFile = new File(file.getAbsolutePath());
		    				if(isFile.exists())
		    				{
		    					iChecker = true; 
		    				}else{
		    					iChecker = false;
		    				}
		    			}
		    			
		    		}
		    	} 
		    	catch (Exception e) 
		    	{
		              e.printStackTrace();
		    	}
		    	
		    	return iChecker;
		    }
	    
}
