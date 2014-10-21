package com.greatspeeches.helper;

import java.lang.reflect.Field;

public class GreateSpeechesUtil {

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
