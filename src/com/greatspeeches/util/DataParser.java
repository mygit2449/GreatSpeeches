package com.greatspeeches.util;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Xml;

import com.greatspeeches.models.HomeDataModel;

public class DataParser {
	
	Context context;
	
	public DataParser(Context context){
		this.context=context;
	}
	
	public ArrayList<HomeDataModel> parser(String type) {
		// TODO Auto-generated method stub
		XmlPullParser parser = Xml.newPullParser();
		ArrayList<HomeDataModel> HomeDataModelList = null;
		try {
	        AssetManager assetManager = context.getAssets();
			InputStream is = assetManager.open(type);
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			HomeDataModel mHomeDataModelObj = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("Menu")) {
						HomeDataModelList = new ArrayList<HomeDataModel>();						
					}else if (name.equalsIgnoreCase("item")) {
						mHomeDataModelObj = new HomeDataModel();					
					}else if (name.equalsIgnoreCase("id")) {
						mHomeDataModelObj.setId(parser.nextText());
					}else if (name.equalsIgnoreCase("name")) {
						mHomeDataModelObj.setName(parser.nextText());
					}else if (name.equalsIgnoreCase("img")) {
						mHomeDataModelObj.setImageId(parser.nextText());
					}else if (name.equalsIgnoreCase("quote")) {
						mHomeDataModelObj.setQuote(parser.nextText());
					}else if (name.equalsIgnoreCase("info")) {
						mHomeDataModelObj.setInfo(parser.nextText());
					}else if (name.equalsIgnoreCase("audio")) {
						mHomeDataModelObj.setAudio(parser.nextText());
					}else if (name.equalsIgnoreCase("video")) {
						mHomeDataModelObj.setVideourl(parser.nextText());
					}else if (name.equalsIgnoreCase("bdate")) {
						mHomeDataModelObj.setbDate(parser.nextText());
					}else if (name.equalsIgnoreCase("ddate")) {
						mHomeDataModelObj.setdDate(parser.nextText());
					}else if (name.equalsIgnoreCase("type")) {
						mHomeDataModelObj.setType(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("item")) {
						HomeDataModelList.add(mHomeDataModelObj);
						mHomeDataModelObj = null;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return HomeDataModelList;
	}

}
