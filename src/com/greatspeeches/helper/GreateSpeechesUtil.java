package com.greatspeeches.helper;

import java.lang.reflect.Field;

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
	 
}
