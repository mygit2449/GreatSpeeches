package com.greatspeeches.helper;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class GreateSpeechesUtil {

	  public static final String 	yOUTUBEdEVELOPERkEY = "AIzaSyBXQz3Hm2vdDThGelmJc6731S9etuzb1Nc";

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
}
