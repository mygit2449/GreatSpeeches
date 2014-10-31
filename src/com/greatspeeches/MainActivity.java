package com.greatspeeches;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
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
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.greatspeeches.helper.PagerSlidingTabStrip;
import com.greatspeeches.helper.ScrollTabHolder;
import com.greatspeeches.helper.ScrollTabHolderFragment;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.notboringactionbar.AlphaForegroundColorSpan;
import com.greatspeeches.notboringactionbar.KenBurnsSupportView;
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
	private ImageView mHeaderLogo;

	private RectF mRect1 = new RectF();
	private RectF mRect2 = new RectF();

	private TypedValue mTypedValue = new TypedValue();
	private SpannableString mSpannableString;
	private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
	
	private int[] _imageaCount = {R.drawable.abraham_lincoln, R.drawable.churchill, R.drawable.frederick_douglass,
			R.drawable.gandhi,R.drawable.john_kennedy,R.drawable.lyndon_johnson,R.drawable.martin_luther_king,R.drawable.patrick_henry,
			R.drawable.reagan,R.drawable.socrates_louvre,R.drawable.susan_anthony};
	
	private ViewFlipper _slideViewFlipper;
    private Handler handler, bgUpdatedHandler;
    private Runnable runnable, bgupdateRunnable; 

    int pickedNumber;
    private List<String> categoriesList = null;
    public  ArrayList<HomeDataModel> homeDataarr = null;

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height);
		mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
		mMinHeaderTranslation = -mMinHeaderHeight + getActionBarHeight();

		setContentView(R.layout.activity_main);

		homeDataarr = parser();
		categoriesList = Arrays.asList(getResources().getStringArray(R.array.categories));
		
		mHeaderPicture = (KenBurnsSupportView) findViewById(R.id.header_picture);
		mHeaderLogo = (ImageView) findViewById(R.id.header_logo);
		mHeader = findViewById(R.id.header);

		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(4);

		mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mPagerAdapter.setTabHolderScrollingContent(this);

		mViewPager.setAdapter(mPagerAdapter);

		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setOnPageChangeListener(this);
		mSpannableString = new SpannableString(getString(R.string.app_name));
		mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(0xffffffff);
		
		ViewHelper.setAlpha(getActionBarIconView(), 0f);
		
		getActionBar().setBackgroundDrawable(null);
		
		_slideViewFlipper = (ViewFlipper)findViewById(R.id.flipper);
		
		
		 for (int i = 0; i < _imageaCount.length; i++) {
				ImageView _img = new ImageView(this);
				_img.setImageResource(_imageaCount[i]);
				_slideViewFlipper.addView(_img);
			}
			
			runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Random rand = new Random();  
					pickedNumber = rand.nextInt(_imageaCount.length)+1;
					updateBeautyBg(pickedNumber);
					handler.postDelayed(runnable, 5000);
					_slideViewFlipper.showNext();
				}
			};
			
			bgupdateRunnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Random rand = new Random();  
					pickedNumber = rand.nextInt(_imageaCount.length)+1;
					updateBeautyBg(pickedNumber);
					bgUpdatedHandler.postDelayed(runnable, 15000);
				}
			};
			
			handler  = new Handler();
			handler.postDelayed(runnable, 5000);
			
			bgUpdatedHandler  = new Handler();
			bgUpdatedHandler.postDelayed(runnable, 15000);
	}

	
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
	
	public ArrayList<HomeDataModel> parser() {
		// TODO Auto-generated method stub
		XmlPullParser parser = Xml.newPullParser();
		ArrayList<HomeDataModel> HomeDataModelList = null;
		try {
	        AssetManager assetManager = getApplicationContext().getAssets();
			InputStream is = assetManager.open("field.xml");
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			HomeDataModel mHomeDataModelObj = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("Menu")) {
						HomeDataModelList = new ArrayList<HomeDataModel>();						
					}else if (name.equalsIgnoreCase("item")) {
						mHomeDataModelObj = new HomeDataModel();					
					}else if (name.equalsIgnoreCase("id")) {
						mHomeDataModelObj.setId(parser.nextText());
					}else if (name.equalsIgnoreCase("name")) {
						mHomeDataModelObj.setName(parser.nextText());
					}else if (name.equalsIgnoreCase("img")) {
						mHomeDataModelObj.setImageId(parser.nextText());
					}else if (name.equalsIgnoreCase("quote")) {
						mHomeDataModelObj.setQuote(parser.nextText());
					}else if (name.equalsIgnoreCase("info")) {
						mHomeDataModelObj.setInfo(parser.nextText());
					}else if (name.equalsIgnoreCase("audio")) {
						mHomeDataModelObj.setAudio(parser.nextText());
					}else if (name.equalsIgnoreCase("video")) {
						mHomeDataModelObj.setVideourl(parser.nextText());
					}else if (name.equalsIgnoreCase("type")) {
						mHomeDataModelObj.setType(parser.nextText());
					}else if (name.equalsIgnoreCase("bdate")) {
						mHomeDataModelObj.setbDate(parser.nextText());
					}else if (name.equalsIgnoreCase("ddate")) {
						mHomeDataModelObj.setdDate(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("item")) {
						HomeDataModelList.add(mHomeDataModelObj);
						mHomeDataModelObj = null;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return HomeDataModelList;
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// nothing
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
		private final String[] TITLES = { "          Popular           ","         Categories        "};
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
}
