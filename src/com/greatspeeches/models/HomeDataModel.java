package com.greatspeeches.models;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeDataModel implements Parcelable{

	public String name = "";
	public String quote = "";
	public String id = "";
	public String imageId = "";
	public String info = "";
	public String audio = "";
	public String videourl = "";
	public String bDate = "";
	public String dDate = "";
	
//	public HomeDataModel(String name,String quote,String id,String imageId,String info,String audio,String videourl,String bDate,String dDate){
//		this.name=name;
//		this.quote=quote;
//		this.id=id;
//		this.imageId=imageId;
//		this.info=info;
//		this.audio=audio;
//		this.videourl=videourl;
//		this.bDate=bDate;
//		this.dDate=dDate;
//	}
	
	public HomeDataModel(){
		
	}
	
	public HomeDataModel(Parcel  in){
		name=in.readString();
		quote=in.readString();
		id=in.readString();
		imageId=in.readString();
		info=in.readString();
		audio=in.readString();
		videourl=in.readString();
		bDate=in.readString();
		dDate=in.readString();
	}
	
	
	/** * * This field is needed for Android to be able to * create new objects, individually or as arrays. * * 
	 * This also means that you can use use the default * constructor to create the object and use another * method to hyrdate it as necessary.
	 *  * * I just find it easier to use the constructor. * It makes sense for the way my brain thinks ;-) 
	 *  * */ 
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { 
		public HomeDataModel createFromParcel(Parcel in) { return new HomeDataModel(in); 
		}  
		public HomeDataModel[] newArray(int size) { 
			return new HomeDataModel[size]; 
		} };

	public String getbDate() {
		return bDate;
	}
	public void setbDate(String bDate) {
		this.bDate = bDate;
	}
	public String getdDate() {
		return dDate;
	}
	public void setdDate(String dDate) {
		this.dDate = dDate;
	}
	public String getVideourl() {
		return videourl;
	}
	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}
	public String getAudio() {
		return audio;
	}
	public void setAudio(String audio) {
		this.audio = audio;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQuote() {
		return quote;
	}
	public void setQuote(String quote) {
		this.quote = quote;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(name);
		dest.writeString(quote);
		dest.writeString(id);
		dest.writeString(imageId);
		dest.writeString(info);
		dest.writeString(audio);
		dest.writeString(videourl);
		dest.writeString(bDate);
		dest.writeString(dDate);

	}

	
}
