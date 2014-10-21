package com.greatspeeches;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		Thread _splashThread = new Thread(null, _splashRunnable);
		_splashThread.start();
	}

	private Runnable _splashRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(1000);
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
			startActivity(new Intent(SplashScreen.this, HomeScreen.class));
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
