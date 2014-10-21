package com.greatspeeches;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.greatspeeches.helper.GreateSpeechesUtil;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.slides.PersonsDescriptionView;

public class HomeScreen extends Activity implements OnClickListener{

	private int[] _imageaCount = {R.drawable.abraham_lincoln, R.drawable.churchill, R.drawable.frederick_douglass,
			R.drawable.gandhi,R.drawable.john_kennedy,R.drawable.lyndon_johnson,R.drawable.martin_luther_king,R.drawable.patrick_henry,
			R.drawable.reagan,R.drawable.socrates_louvre,R.drawable.susan_anthony};
	
	private ViewFlipper _slideViewFlipper;
	private int iCtr = 0;
    private Handler handler;
    private Runnable runnable; 
    public static ArrayList<HomeDataModel> homeDataarr = null;
    private ListView popularList;
    public static Typeface arimoype,alextype;
    private RadioButton popular, categories;
    private ArrayList<String> categoriesList = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		_slideViewFlipper = (ViewFlipper)findViewById(R.id.flipper);
		
		alextype = Typeface.createFromAsset(getAssets(),"AlexBrush-Regular.ttf"); 
		arimoype = Typeface.createFromAsset(getAssets(),"Arimo-Regular.ttf");
		
	
		popular = (RadioButton)findViewById(R.id.first_radio);
		categories = (RadioButton)findViewById(R.id.second_radio);
		popular.setChecked(true);
		popular.setOnClickListener(this);
		categories.setOnClickListener(this);
		
		homeDataarr = parser();
		
		popularList = (ListView)findViewById(R.id.home_list);
		
		popularList.setAdapter(new PopularListAdapter(HomeScreen.this, 0));
		
		popularList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (popular.isChecked()) {
					startActivity(new Intent(HomeScreen.this, PersonsDescriptionView.class).putExtra("position", arg2));
				}
			}
		});
		
		 for (int i = 0; i < _imageaCount.length; i++) {
				ImageView _img = new ImageView(this);
				_img.setImageResource(_imageaCount[i]);
				_slideViewFlipper.addView(_img);
			}
			
			runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.postDelayed(runnable, 3000);
					_slideViewFlipper.showNext();
				}
			};
			
			handler  = new Handler();
			handler.postDelayed(runnable, 3000);
	}

	 RefreshHandler refreshHandler=new RefreshHandler();
	    class RefreshHandler extends Handler{
	        @Override
	        public void handleMessage(Message msg) {
	            // TODO Auto-generated method stub
	            updateUI();
	        }
	        public void sleep(long delayMillis){
	            this.removeMessages(0);
	            sendMessageDelayed(obtainMessage(0), delayMillis);
	        }
	    };
	    
	    public void updateUI(){
	            refreshHandler.sleep(10000);
	            if(iCtr<_imageaCount.length){
	                iCtr++;
	        }
	    }
	    
	    public ArrayList<HomeDataModel> parser() {
			// TODO Auto-generated method stub
			XmlPullParser parser = Xml.newPullParser();
			ArrayList<HomeDataModel> HomeDataModelList = null;
			try {
		        AssetManager assetManager = getApplicationContext().getAssets();
				InputStream is = assetManager.open("field.xml");
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
	    
	    
	    class PopularListAdapter extends BaseAdapter{
	    	
	    	Context context;
	    	int type;
	    	public PopularListAdapter(Context context, int type) {
	    		super();
	    		this.context=context;
	    		this.type=type;
	        }

		

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				
				if (convertView == null) {
					LayoutInflater mInflater= LayoutInflater.from(context);
					if (type == 0) {
						convertView = mInflater.inflate(R.layout.popular_list_row, null);
					}else{
						convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
					}
				}
				 
				if (type == 0) {
					try {
						TextView personName = (TextView)convertView.findViewById(R.id.person_name);
						ImageView personImage = (ImageView)convertView.findViewById(R.id.popular_img);
						TextView personQuote = (TextView)convertView.findViewById(R.id.person_quote);
						personName.setText(""+homeDataarr.get(position).getName());
						personName.setTypeface(alextype);
						personQuote.setTypeface(arimoype);
						personQuote.setText(""+homeDataarr.get(position).getQuote());
						personImage.setBackgroundResource(GreateSpeechesUtil.getResId(homeDataarr.get(position).getImageId(), R.drawable.class));
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}else{
					TextView categoryName = (TextView)convertView.findViewById(android.R.id.text1);
					categoryName.setTypeface(arimoype);
					categoryName.setText(""+categoriesList.get(position));
					categoryName.setTextColor(Color.parseColor("#ffffff"));
				}
				
				 
				 return convertView;
			}


			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				int count;
				if (type == 0) {
					count = homeDataarr.size();
				}else{
					count = categoriesList.size();
				}
				return count;
			}


			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}



			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				Object item;
				if (type == 0) {
					item = homeDataarr.get(position);
				}else{
					item = categoriesList.get(position);
				}
				return item;
			}
			
		}
	    
	 	@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.first_radio:
				popularList.setAdapter(new PopularListAdapter(HomeScreen.this, 0));
				break;
			case R.id.second_radio:
				
				categoriesList = new ArrayList<String>();  
				categoriesList.add("Science & Technology");
				categoriesList.add("Sports");
				categoriesList.add("Entertainment");
				categoriesList.add("Politics");
				categoriesList.add("Women's History");
				popularList.setAdapter(new PopularListAdapter(HomeScreen.this, 1));
				
				break;
			default:
				break;
			}
		}
}
