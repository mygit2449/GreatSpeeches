package com.greatspeeches.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;

@SuppressLint("Wakelock")
public class AudioDownloaderTask extends AsyncTask<String, Integer, String>
{

    private Context context;
	private ProgressDialog mDialog;
	private String urlHere,  exceptionStr = "";
	private Handler play;
	
    public AudioDownloaderTask(Context context, String urlHere, Handler play)
    {
    	this.play=play;
        this.context = context;
        this.mDialog = ProgressDialog.show(context, "", "Loading audio..", true, false);
		this.mDialog.setCancelable(true);
		this.urlHere=urlHere;
    }

    @SuppressWarnings("resource")
    @Override 
    protected String doInBackground(String... sUrl)
    {
     
    	String audioFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/GreatLives";
		 
		File audioDirectory=new File(audioFolderPath);

        //prevent CPU from going off if the user presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wl.acquire();

        //download
        try
        {
        	if(!audioDirectory.exists()){
        		audioDirectory.mkdirs();
			 }
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try
            {
                //connect to url
                URL url = new URL(urlHere);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                // check for http_ok (200)
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();

                int indexPos = urlHere.lastIndexOf("/");
                String fileName = urlHere.substring(indexPos+1, urlHere.length());
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(audioDirectory+"/"+fileName);//change extension

                //copying
                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1)
                {
                    // allow canceling
                    if (isCancelled())
                    {
                        new File(audioDirectory+"/"+fileName).delete();//delete partially downloaded file
                        return null;
                    }
                    
                    output.write(data, 0, count);
                }
            }
            catch (Exception e)
            {
            	exceptionStr = e.toString();
                return e.toString();
            }
            finally //closing streams and connection
            {
                try
                {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                }
                catch (IOException ignored)
                {
                	exceptionStr = ignored.toString();
                }

                if (connection != null)
                    connection.disconnect();
            }
        }
        finally
        {
            wl.release(); // release the lock screen
        }
        return null;
    }


    @Override
    protected void onPostExecute(String result)
    {
    	try {
			 mDialog.dismiss();
			 mDialog = null;
			 play.sendEmptyMessage(2);
		 } catch (Exception e) {
		        // nothing
			 exceptionStr = e.toString();
		  }
    	
    	if (exceptionStr.length() > 0) {
    		play.sendEmptyMessage(4);
		}
    }
    
    
}

