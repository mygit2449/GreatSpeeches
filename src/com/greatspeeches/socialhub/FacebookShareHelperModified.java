package com.greatspeeches.socialhub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.greatspeeches.socialhub.Facebook.DialogListener;

public class FacebookShareHelperModified{
	

//	private static final String apiKey="475769939104467";  //   "374967135897431";
//	private static final String[] PERMISSION=new String[]{"publish_stream"};
	
//	private static final String TOKEN="access_token";
//	private static final String EXPIRES="expire_in";
//	private static final String KEY="facebook_credentials";
//	
	private Facebook facebook;
//	private AsyncFacebookRunner asyncfacebookrunner;
	private String messageToPost, mTitle, shareUrl;
	
	String name, imgUrl;
	private Activity activity;

	String[] PERMISSION;
	
	public FacebookShareHelperModified(Activity activity, String messageToPost, String mTitle, String shareUrl, String apiKey, String[] PERMISSION, String name, String imgUrl)
	{
		facebook = new Facebook(apiKey);
		
		this.activity=activity;
		if(messageToPost==null)
		{
			this.mTitle = "Test";
			this.messageToPost="";
			
		}else{
			
			this.mTitle = mTitle;
			this.messageToPost=messageToPost;
			this.shareUrl= shareUrl;
		}
		
		
		this.imgUrl = imgUrl;
		this.name=name;
		this.PERMISSION = PERMISSION;
		
		if(facebook.isSessionValid())
		{	
			postToWall(this.messageToPost, this.mTitle, this.shareUrl);
			
		}else{
			
			loginAndPostToWall();
		}
	
		
	}
	
	public void loginAndPostToWall()
	{	
		
		 facebook.authorize(activity, PERMISSION, new LoginDialogListener());
	}

	public void postToWall(String message, String title, String shareUrl)
	{	

		
		
		
		 Bundle parameters = new Bundle();
		    parameters.putString("method", "stream.publish");

		    JSONObject attachment = new JSONObject();

		            // for adding image to Dialog       
		    try {
		        JSONObject media = new JSONObject();
		        media.put("type", "image");
		        media.put("src", imgUrl);
		        media.put("href", imgUrl);
		        attachment.put("media", new JSONArray().put(media));
		    } catch (JSONException e1) {
		    }

		            // End if Image attachment

		            // for adding Message with URL link
		    try {
		    	
		    	
		    	attachment.put("message", "Check out what I found on Test!");
		        attachment.put("name", title);
		        attachment.put("href", shareUrl);
		        attachment.put("caption", "Test App");
		        attachment.put("description", message);
//			    attachment.put("name", "Check out what I found on DaleeloApp! "+mTitle);
//			    attachment.put("href", message);
			    
		    } catch (JSONException e) {
		    }

		    parameters.putString("attachment", attachment.toString());
		    facebook.dialog(activity, "stream.publish", parameters, new WallPostDialogListener());
  
	}
	
	class LoginDialogListener implements DialogListener
	{

		@Override
		public void onComplete(Bundle values) 
		{
			
		
			if(FacebookShareHelperModified.this.messageToPost!=null)
			{
				postToWall(
						FacebookShareHelperModified.this.messageToPost, 
						FacebookShareHelperModified.this.mTitle,
						FacebookShareHelperModified.this.shareUrl);
			}
		}

		@Override
		public void onFacebookError(FacebookError e) 
		{
			
		}

		@Override
		public void onError(DialogError e) 
		{
			
		}

		@Override
		public void onCancel() 
		{
			
		}
	
	}
	
	class WallPostDialogListener implements DialogListener
	{
		
		@Override
		public void onComplete(Bundle values) {
			final String post_ID=values.getString("post_id");
			if(post_ID != null)
			{
				Toast.makeText(FacebookShareHelperModified.this.activity, "Post successfully", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(FacebookShareHelperModified.this.activity, "Post unsuccessful", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onFacebookError(FacebookError e) {
			
		}

		@Override
		public void onError(DialogError e) {
			
		}

		@Override
		public void onCancel() {
			
		}
		
	}
	

}