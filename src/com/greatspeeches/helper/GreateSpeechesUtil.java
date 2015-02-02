package com.greatspeeches.helper;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

public class GreateSpeechesUtil {

	  public static final String 	yOUTUBEdEVELOPERkEY = "AIzaSyBXQz3Hm2vdDThGelmJc6731S9etuzb1Nc";
	  public  static final String AUDIO_LINK = "https://81ccbf467f07ea9920c08612ae7f36a6c5656fbf.googledrive.com/host/0Byk9-iyvqqzcbF9GSDdHYU81Rms/";

	 public static int getResId(String variableName, Class<?> c) {
	    	
	        try {
	            Field idField = c.getDeclaredField(variableName);
	            return idField.getInt(idField);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1;
	        } 
	    }
	 
	 
	 public static float convertDpToPixel(float dp, Context context){
		    Resources resources = context.getResources();
		    DisplayMetrics metrics = resources.getDisplayMetrics();
		    float px = dp * (metrics.densityDpi / 160f);
		    return px;
		} 
	 
	
	 public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,int reqWidth, int reqHeight) {
				// First decode with inJustDecodeBounds=true to check dimensions
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(res, resId, options);
				// Calculate inSampleSize
				options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
				// Decode bitmap with inSampleSize set
				options.inJustDecodeBounds = false;
				return BitmapFactory.decodeResource(res, resId, options);
	 }
			 
			 
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;
			if (height > reqHeight || width > reqWidth) {
			    // Calculate ratios of height and width to requested height and width
			    final int heightRatio = Math.round((float) height / (float) reqHeight);
			    final int widthRatio = Math.round((float) width / (float) reqWidth);
			    // Choose the smallest ratio as inSampleSize value, this will guarantee
			    // a final image with both dimensions larger than or equal to the
			    // requested height and width.
			    inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
			 
			return inSampleSize;
	}
}
