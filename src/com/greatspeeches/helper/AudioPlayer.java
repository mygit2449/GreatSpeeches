package com.greatspeeches.helper;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class AudioPlayer extends MediaPlayer{

	public  AudioPlayer(){
		
	}
	
	
	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return super.getDuration();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return super.isPlaying();
	}

	@Override
	public void pause() throws IllegalStateException {
		// TODO Auto-generated method stub
		super.pause();
	}

	@Override
	public void prepare() throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		super.prepare();
	}

	@Override
	public void prepareAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		super.prepareAsync();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		super.release();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
	}

	@Override
	public void seekTo(int msec) throws IllegalStateException {
		// TODO Auto-generated method stub
		super.seekTo(msec);
	}

	@Override
	public void selectTrack(int index) throws IllegalStateException {
		// TODO Auto-generated method stub
		super.selectTrack(index);
	}

	@Override
	public void setAudioSessionId(int sessionId)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setAudioSessionId(sessionId);
	}

	@Override
	public void setDataSource(Context context, Uri uri,
			Map<String, String> headers) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setDataSource(context, uri, headers);
	}

	@Override
	public void setDataSource(Context context, Uri uri) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setDataSource(context, uri);
	}

	@Override
	public void setDataSource(FileDescriptor fd, long offset, long length)
			throws IOException, IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setDataSource(fd, offset, length);
	}

	@Override
	public void setDataSource(FileDescriptor fd) throws IOException,
			IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setDataSource(fd);
	}

	@Override
	public void setDataSource(String path) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setDataSource(path);
	}

	@Override
	public void start() throws IllegalStateException {
		// TODO Auto-generated method stub
		super.start();
	}

	@Override
	public void stop() throws IllegalStateException {
		// TODO Auto-generated method stub
		super.stop();
	}

}
