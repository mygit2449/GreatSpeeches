package com.greatspeeches.socialhub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.greatspeeches.R;
import com.greatspeeches.util.ConnectionDetector;

public class TwitActivity extends Activity {

	private WebView wv = null;
	private ProgressDialog pDialog;

	private LinearLayout _loadingLL = null;
	
	static String TWITTER_CONSUMER_KEY = "OvTiZpfMEI442aY1tPHrwQ"; // place your cosumer key here
	static String TWITTER_CONSUMER_SECRET = "LKSg5iyz3VtxSnvYZa7cBJ1XTK1GOCeSxBp2kURSqYI"; // place your consumer secret here

	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

//	public static final String CALLBACK_URL = "oauth://connect";
	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	
	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;
	static final String TWITTER_CALLBACK_URL = "oauth://sample";

		
	// Shared Preferences
	private static SharedPreferences mSharedPreferences;
		
	// Internet Connection detector
	private ConnectionDetector cd;
	public long userID;
	public  User user;
	public  String username = null;
	public AccessToken accessToken;
	private Intent _receiverIntent;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twit);
		_receiverIntent = getIntent();
		wv = (WebView)findViewById(R.id.twit_web);
		wv.getSettings().setJavaScriptEnabled(true);
        wv.requestFocus(View.FOCUS_DOWN);
        wv.getSettings().setSaveFormData(false);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        
        _loadingLL = (LinearLayout)findViewById(R.id.progress_dlg);
		mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

		cd = new ConnectionDetector(TwitActivity.this);
		
        new  Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				    ConfigurationBuilder builder = new ConfigurationBuilder();
					builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
					builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
					Configuration configuration = builder.build();
					
					TwitterFactory factory = new TwitterFactory(configuration);
					twitter = factory.getInstance();

					try {
						requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
					        wv.loadUrl(requestToken.getAuthenticationURL());
					        runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									_loadingLL.setVisibility(View.GONE);
								}
							});
						}
					});
			}
		}).start();
        
  
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            	Uri uri = Uri.parse(url);
                final String verifier = uri.getQueryParameter("oauth_verifier");
				Log.e("Twitter OAuth Token...", "> ");
				Log.v(getClass().getSimpleName(), "verifier.accessToken.."+url+"..verifier..."+verifier);
				if(verifier != null){
					new getToken(TwitActivity.this, verifier).execute();
				}
				return false;
                
            }
        });
        
	}
	
	class getToken extends  AsyncTask<String, String, String>{
		String verifier;
		Context context;
		public getToken(Context contex,  String verifier){
			this.verifier=verifier;
			this.context=contex;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			
			Editor e = mSharedPreferences.edit();
			// After getting access token, access token secret
			// store them in application preferences
			e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
			e.putString(PREF_KEY_OAUTH_SECRET,accessToken.getTokenSecret());
			// Store login status - true
			e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
			e.commit(); // save changes
			Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
			
			
			_loadingLL.setVisibility(View.GONE);
			if (cd.isConnectingToInternet()) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						wv.setVisibility(View.GONE);
						updateTwitterStatus _twit = new updateTwitterStatus(TwitActivity.this);
						_twit.execute("\n\n"+_receiverIntent.getStringExtra("quote")+"\n\n");						}
				});
			}
			
		}
		
	}
	
	/**
	 * Function to update status
	 * */
	class updateTwitterStatus extends AsyncTask<String, String, String> {
		
		Context _cc = null;
		File dummyFile = null;
		public updateTwitterStatus(Context _cc){
			this._cc=_cc;
		}

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			pDialog = new ProgressDialog(_cc);
//			pDialog.setMessage("Updating to twitter...");
//			pDialog.setIndeterminate(false);
//			pDialog.setCancelable(false);
//			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			Log.d("Tweet Text", "> " + args[0]);
			String message = args[0];
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
				
				// Access Token 
				String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
				// Access Token Secret
				String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
				
				AccessToken accessToken = new AccessToken(access_token, access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
				StatusUpdate status = new StatusUpdate(message);
				dummyFile = new File(getCacheDir()+"/"+_receiverIntent.getStringExtra("img_name")+".jpg");
				 
				if (!dummyFile.exists()) try {

				    InputStream is = getAssets().open("images/"+_receiverIntent.getStringExtra("img_name")+".jpg");
				    int size = is.available();
				    byte[] buffer = new byte[size];
				    is.read(buffer);
				    is.close();


				    FileOutputStream fos = new FileOutputStream(dummyFile);
				    fos.write(buffer);
				    fos.close();
				  } catch (Exception e) { throw new RuntimeException(e); }
				 
		
			        status.setMedia(dummyFile);
			        twitter4j.Status response = twitter.updateStatus(status);
			
				Log.d("Status", "> " + response.getText());
			} catch (TwitterException e) {
				// Error in updating status
				Log.d("Twitter Update Error", e.getMessage());
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog and show
		 * the data in UI Always use runOnUiThread(new Runnable()) to update UI
		 * from background thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			dummyFile.delete();
			Toast.makeText(getApplicationContext(),"Status tweeted successfully..", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	@Override
	protected void onDestroy() {
	    if (pDialog != null) {
	        if (pDialog.isShowing()) {
	        	pDialog.dismiss();
	        	pDialog = null;
	        }
	    }
	    super.onDestroy();
	}
	
	
}
