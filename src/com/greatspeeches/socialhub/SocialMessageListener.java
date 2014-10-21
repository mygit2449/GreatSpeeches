package com.greatspeeches.socialhub;

public interface SocialMessageListener {

	public void onComplete(String message);
	public void onError(String message);
}
