package com.greatspeeches.video;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.greatspeeches.R;


public class CustomWebView extends WebView {
	
	private Context 							mContext;
	private MyWebChromeClient					mWebChromeClient;
	private View								mCustomView;
	private FrameLayout							mCustomViewContainer;
	private WebChromeClient.CustomViewCallback 	mCustomViewCallback;
	
	private FrameLayout							mContentView;
	private RelativeLayout						mBrowserFrameLayout;
	private FrameLayout							mLayout;
	
    static final String LOGTAG = "CustomWebView";
	    
	private void init(Context context) {
		mContext = context;		
		Activity a = (Activity) mContext;
		
		mLayout = new FrameLayout(context);
		
		mBrowserFrameLayout = (RelativeLayout) LayoutInflater.from(a).inflate(R.layout.custom_screen, null);
		mContentView = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.main_content);
		mCustomViewContainer = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.fullscreen_custom_content);
		
		mLayout.addView(mBrowserFrameLayout, COVER_SCREEN_PARAMS);

		mWebChromeClient = new MyWebChromeClient();
	    setWebChromeClient(mWebChromeClient);
	    
	    setWebViewClient(new MyWebViewClient());
	       
	    // Configure the webview
	    WebSettings s = getSettings();
	    s.setBuiltInZoomControls(true);
	    s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
	    s.setUseWideViewPort(true);
	    s.setLoadWithOverviewMode(true);
	    s.setSaveFormData(true);
	    s.setJavaScriptEnabled(true); 
	    
	    // enable Web Storage: localStorage, sessionStorage
	    s.setDomStorageEnabled(true);
        CustomWebView.this.setBackgroundColor(Color.BLACK);

	    mContentView.addView(this);

	}

	public CustomWebView(Context context) {
		super(context);
		init(context);
	}

	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public FrameLayout getLayout() {
		return mLayout;
	}
	
    public boolean inCustomView() {
		return (mCustomView != null);
	}
    
    public void hideCustomView() {
		mWebChromeClient.onHideCustomView();
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if ((mCustomView == null) && canGoBack()){
    			goBack();
    			return true;
    		}
    	}
    	return super.onKeyDown(keyCode, event);
    }

    private class MyWebChromeClient extends WebChromeClient {
		private Bitmap 		mDefaultVideoPoster;
		private View 		mVideoProgressView;
    	
    	@Override
		public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback)
		{
    		
    		try {
    			CustomWebView.this.setVisibility(View.GONE);
    			// if a view already exists then immediately terminate the new one
    			if (mCustomView != null) {
    				callback.onCustomViewHidden();
    				return;
    			}
    			
    			mCustomViewContainer.addView(view);
    			mCustomView = view;
    			mCustomViewCallback = callback;
    			mCustomViewContainer.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				// TODO: handle exception
			}
			//Log.i(LOGTAG, "here in on ShowCustomView");
		}
		
		@Override
		public void onHideCustomView() {
			
			
			try {
				if (mCustomView == null)
					return;	       
				
				// Hide the custom view.
				mCustomView.setVisibility(View.GONE);
				
				// Remove the custom view from its container.
				mCustomViewContainer.removeView(mCustomView);
				mCustomView = null;
				mCustomViewContainer.setVisibility(View.GONE);
				mCustomViewCallback.onCustomViewHidden();
				
				CustomWebView.this.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
	        //Log.i(LOGTAG, "set it to webVew");
		}
		
		@Override
		public Bitmap getDefaultVideoPoster() {
			//Log.i(LOGTAG, "here in on getDefaultVideoPoster");	
			if (mDefaultVideoPoster == null) {
				mDefaultVideoPoster = BitmapFactory.decodeResource(getResources(), R.drawable.default_video_poster);
		    }
			return mDefaultVideoPoster;
		}
		
		@Override
		public View getVideoLoadingProgressView() {
			//Log.i(LOGTAG, "here in on getVideoLoadingPregressView");
			
	        if (mVideoProgressView == null) {
	            LayoutInflater inflater = LayoutInflater.from(mContext);
	            mVideoProgressView = inflater.inflate(R.layout.video_loading_progress, null);
	        }
	        return mVideoProgressView; 
		}
    	
    	 @Override
         public void onReceivedTitle(WebView view, String title) {
            ((Activity) mContext).setTitle(title);
         }

         @Override
         public void onProgressChanged(WebView view, int newProgress) {
        	 ((Activity) mContext).getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress*100);
         }
         
         @Override
         public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
             callback.invoke(origin, true, false);
         }
         
         
    }
	
	private class MyWebViewClient extends WebViewClient {
	    @Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

		}

		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	Log.i(LOGTAG, "shouldOverrideUrlLoading: "+url);
	    	// don't override URL so that stuff within iframe can work properly
	        // view.loadUrl(url);
	    	
//	    	if (url.endsWith(".mp4") )
//            {                           
//                Intent intent = new Intent(mContext, VideoPlayer.class);
//                intent.putExtra("url", url);
//                mContext.startActivity(intent);
//                return true;
//            } 
//            else 
//            {
//            }
//	    	
	    	return super.shouldOverrideUrlLoading(view, url);
	    	
	    	
//	        return false;
	    }
	    
	   
	}
	
	static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
        new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
}