package com.greatspeeches;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.greatspeeches.database.GreatLivesDataBaseHelper;
import com.greatspeeches.helper.PagerSlidingTabStrip;
import com.greatspeeches.helper.ScrollTabHolder;
import com.greatspeeches.helper.ScrollTabHolderFragment;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.notboringactionbar.AlphaForegroundColorSpan;
import com.greatspeeches.notboringactionbar.KenBurnsSupportView;
import com.greatspeeches.receivers.NotificationReceiver;
import com.greatspeeches.slides.PersonsDescriptionView;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends FragmentActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener {

	private static AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();

	private KenBurnsSupportView mHeaderPicture;
	private View mHeader;

	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter;

	private int mActionBarHeight;
	private int mMinHeaderHeight;
	private int mHeaderHeight;
	private int mMinHeaderTranslation;

	private RectF mRect1 = new RectF();
	private RectF mRect2 = new RectF();

	private TypedValue mTypedValue = new TypedValue();
	private SpannableString mSpannableString;
	private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
	
	private int[] _imageaCount = {R.drawable.mikhail,R.drawable.john_kennedy,
			R.drawable.gandhi,R.drawable.william,R.drawable.helen,R.drawable.anne,R.drawable.patrick_henry,R.drawable.churchill,R.drawable.martin_hh,R.drawable.lyndon_johnson,R.drawable.reagan,
			R.drawable.swami_hh,R.drawable.abraham_lincoln,R.drawable.sri,R.drawable.nelson};
	
	private ViewFlipper _slideViewFlipper;
    private Handler handler, bgUpdatedHandler;
    private Runnable runnable;
    public List<String> categoriesList = null;
    public  ArrayList<HomeDataModel> homeDataarr = null;
    private FragmentManager fManager;
    private boolean doubleback = false;

    private GreatLivesDataBaseHelper mDataBaseHelper = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height);
		mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
		mMinHeaderTranslation = -mMinHeaderHeight + getActionBarHeight();
		setContentView(R.layout.activity_main);

		mDataBaseHelper = new GreatLivesDataBaseHelper(MainActivity.this);
		mDataBaseHelper.openDataBase();
		
		fManager = getSupportFragmentManager();
		
		if (null != getIntent() && getIntent().getAction().equalsIgnoreCase("fromNotification")) {
			getIntent().setAction("Noneed");
			handleNotificationLaunch(getIntent());
		}else{
			forFirstStep();
		}
		
	}


	public void forFirstStep(){
		homeDataarr = mDataBaseHelper.getData("Popular",0);
		categoriesList = Arrays.asList(getResources().getStringArray(R.array.categories));
		
		mHeaderPicture = (KenBurnsSupportView) findViewById(R.id.header_picture);
		mHeader = findViewById(R.id.header);
		mHeaderPicture.setResourceIds(R.drawable.parallax_gs1, R.drawable.parallax_gs2);

		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(4);

		mPagerAdapter = new PagerAdapter(fManager);
		mPagerAdapter.setTabHolderScrollingContent(this);

		mViewPager.setAdapter(mPagerAdapter);
		if (null != getIntent() && getIntent().getAction().equalsIgnoreCase("fromSplash")) {
			mViewPager.setCurrentItem(0);
		}else if(null != getIntent() && getIntent().getAction().equalsIgnoreCase("fromCat")){
			mViewPager.setCurrentItem(1);
		}
		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setOnPageChangeListener(this);
		mSpannableString = new SpannableString(getString(R.string.app_name));
		mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(0xffffffff);
		
		ViewHelper.setAlpha(getActionBarIconView(), 0f);
		
		getActionBar().setBackgroundDrawable(null);
		
		_slideViewFlipper = (ViewFlipper)findViewById(R.id.flipper);
		
		
		 for (int i = 0; i < _imageaCount.length; i++) {
				ImageView _img = new ImageView(this);
				_img.setTag(i);
				_img.setImageResource(_imageaCount[i]);
				_img.setOnClickListener(flipClickListener);
				_slideViewFlipper.addView(_img);
			}
			
			runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.postDelayed(runnable, 5000);
					_slideViewFlipper.showNext();
				}
			};
	
			handler  = new Handler();
			handler.postDelayed(runnable, 5000);
			
			bgUpdatedHandler  = new Handler();
			bgUpdatedHandler.postDelayed(runnable, 10000);
			
			SharedPreferences prefs = getSharedPreferences("gl", Context.MODE_PRIVATE);
			boolean isFirstTrack = prefs.getBoolean("firstRem", true);
			if(isFirstTrack){
				SharedPreferences.Editor preferencesEdit;
				preferencesEdit = prefs.edit();
				preferencesEdit.putBoolean("firstRem",false);
				preferencesEdit.commit();
				setQoutationReminders();
			}
		
	}
	
	OnClickListener flipClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (null != v) {
				startActivity(new Intent(MainActivity.this, PersonsDescriptionView.class).putExtra("position", (Integer) v.getTag()).putParcelableArrayListExtra("popularItems", homeDataarr).setAction("fromPop"));
			}
			
		}
	};
	
	
	public void updateBeautyBg(int forimg){
		int nextImg;
		if(forimg < _imageaCount.length){
			if(forimg+1 < _imageaCount.length){
				nextImg = forimg+1;
			}else{
				nextImg = forimg-1;
			}
			if((nextImg > -1 && nextImg < _imageaCount.length) && (forimg > -1 && forimg < _imageaCount.length)){
				mHeaderPicture.setResourceIds(_imageaCount[forimg], _imageaCount[nextImg]);
			}
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// nothing
	}
	
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	
public void setQoutationReminders(){
		alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE); 
		Intent intent = new Intent(this, NotificationReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0); 
		// Set the alarm to start at approximately 8:00 a.m. 
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24, alarmIntent); 		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// nothing
	}

	@Override
	public void onPageSelected(int position) {
		SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mPagerAdapter.getScrollTabHolders();
		ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);

		currentHolder.adjustScroll((int) (mHeader.getHeight() + ViewHelper.getTranslationY(mHeader)));
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
		if (mViewPager.getCurrentItem() == pagePosition) {
			int scrollY = getScrollY(view);
			ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, mMinHeaderTranslation));
			float ratio = clamp(ViewHelper.getTranslationY(mHeader) / mMinHeaderTranslation, 0.0f, 1.0f);
			interpolate(_slideViewFlipper, getActionBarIconView(), sSmoothInterpolator.getInterpolation(ratio));
			setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
		}
	}

	@Override
	public void adjustScroll(int scrollHeight) {
		// nothing
	}

	public int getScrollY(AbsListView view) {
		View c = view.getChildAt(0);
		if (c == null) {
			return 0;
		}

		int firstVisiblePosition = view.getFirstVisiblePosition();
		int top = c.getTop();

		int headerHeight = 0;
		if (firstVisiblePosition >= 1) {
			headerHeight = mHeaderHeight;
		}

		return -top + firstVisiblePosition * c.getHeight() + headerHeight;
	}

	public static float clamp(float value, float max, float min) {
		return Math.max(Math.min(value, min), max);
	}

	private void interpolate(View view1, View view2, float interpolation) {
		getOnScreenRect(mRect1, view1);
		getOnScreenRect(mRect2, view2);

		float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
		float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
		float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
		float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

		ViewHelper.setTranslationX(view1, translationX);
		ViewHelper.setTranslationY(view1, translationY - ViewHelper.getTranslationY(mHeader));
		ViewHelper.setScaleX(view1, scaleX);
		ViewHelper.setScaleY(view1, scaleY);
	}

	private RectF getOnScreenRect(RectF rect, View view) {
		rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
		return rect;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public int getActionBarHeight() {
		if (mActionBarHeight != 0) {
			return mActionBarHeight;
		}
		
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
			getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
		}else{
			getTheme().resolveAttribute(R.attr.actionBarSize, mTypedValue, true);
		}
		
		mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
		
		return mActionBarHeight;
	}

	private void setTitleAlpha(float alpha) {
		mAlphaForegroundColorSpan.setAlpha(alpha);
		mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getActionBar().setTitle(mSpannableString);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private ImageView getActionBarIconView() {
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			return (ImageView)findViewById(android.R.id.home);
		}

		return (ImageView)findViewById(android.support.v7.appcompat.R.id.home);
	}

	public class PagerAdapter extends FragmentPagerAdapter {

		private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
		private final String[] TITLES = { "Popular","Categories"};
		private ScrollTabHolder mListener;

		public PagerAdapter(FragmentManager fm) {
			super(fm);
			mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
		}

		public void setTabHolderScrollingContent(ScrollTabHolder listener) {
			mListener = listener;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			ScrollTabHolderFragment fragment = null;
			if(position == 0){
				fragment = (ScrollTabHolderFragment) MainListFragmentListFragment.newInstance(position, homeDataarr);
			}else{
				fragment = (ScrollTabHolderFragment) MainListFragmentListFragment.newInstance(position, categoriesList);
			}

			mScrollTabHolders.put(position, fragment);
			if (mListener != null) {
				fragment.setScrollTabHolder(mListener);
			}

			return fragment;
		}

		public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
			return mScrollTabHolders;
		}

	}
	
	public void handleNotificationLaunch(Intent receiverIntent){
		int selectedPos = -1;
		selectedPos = receiverIntent.getExtras().getInt("selectedPos");
		homeDataarr = mDataBaseHelper.getData("",selectedPos);
		selectedPos = getIndex(selectedPos);
		
		if (null != homeDataarr && selectedPos > -1 && selectedPos <= homeDataarr.size()) {
				startActivity(new Intent(this, PersonsDescriptionView.class).putExtra("position", selectedPos).putParcelableArrayListExtra("popularItems", homeDataarr).setAction("fromPop"));
				finish();
		}
	}
	
	@Override
 	public void onBackPressed() {
 		if (doubleback) {
 			android.app.FragmentManager fm = getFragmentManager();
 			for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
 			    fm.popBackStack();
 			}
 			finish();
        } else {
        	doubleback = true;
            Toast.makeText(MainActivity.this, "Press the back key again to close the app.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                	doubleback = false;
                }
            }, 2000);
        }
 	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(null != mDataBaseHelper)
			mDataBaseHelper.close();
	}
	
	
	public int getIndex(int inxPos){
		for (int indexPos = 0; indexPos < homeDataarr.size() ; indexPos++) {
			if(inxPos == Integer.parseInt(homeDataarr.get(indexPos).getId())){
				return indexPos;
			}
		}
		return inxPos;
	}
	
}
