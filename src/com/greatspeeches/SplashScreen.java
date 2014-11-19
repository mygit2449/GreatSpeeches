package com.greatspeeches;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity {

	private ImageView animImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		Thread _splashThread = new Thread(null, _splashRunnable);
		_splashThread.start();
		
		animImg = (ImageView)findViewById(R.id.sample_img);
		animImg.setVisibility(View.GONE);
		 final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
	     animImg.startAnimation(animationFadeIn);
	     
	     animationFadeIn.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				animImg.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
	}

	private Runnable _splashRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			runOnUiThread(_startActivityRunnable);
		}
	};
	
	
	private Runnable _startActivityRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			startActivity(new Intent(SplashScreen.this, MainActivity.class).setAction("fromSplash"));
			finish();
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
