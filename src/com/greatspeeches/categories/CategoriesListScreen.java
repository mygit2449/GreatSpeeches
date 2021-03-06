package com.greatspeeches.categories;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.greatspeeches.R;
import com.greatspeeches.database.GreatLivesDataBaseHelper;
import com.greatspeeches.helper.GreateSpeechesUtil;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.slides.PersonsDescriptionView;

public class CategoriesListScreen extends Activity {

	private TypedArray imagesHere = null;
	private String typeStr = "";

    private ViewFlipper slideViewFlipper;
    private Handler handler;
    private Runnable runnable; 
    public  ArrayList<HomeDataModel> catDatarr = null;
    private ListView popularList;
    private GreatLivesDataBaseHelper mDataBaseHelper = null;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories_list_screen);
		
		typeStr = getIntent().getStringExtra("categoryType");
		mDataBaseHelper = new GreatLivesDataBaseHelper(CategoriesListScreen.this);
		mDataBaseHelper.openDataBase();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(typeStr);
//		getActionBar().hide();
		
		catDatarr = mDataBaseHelper.getData(typeStr,0);
		if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category1))){
			imagesHere = getResources().obtainTypedArray(R.array.science);
		}else if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category2))){
			imagesHere = getResources().obtainTypedArray(R.array.sports);
		}else if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category3))){
			imagesHere = getResources().obtainTypedArray(R.array.cultural);
		}else if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category4))){
			imagesHere = getResources().obtainTypedArray(R.array.politicians);
		}else if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category5))){
			imagesHere = getResources().obtainTypedArray(R.array.womens);
		}
		
		
		slideViewFlipper = (ViewFlipper)findViewById(R.id.flipper);
	
		
		 for (int i = 0; i < imagesHere.length(); i++) {
				ImageView img = new ImageView(this);
				img.setBackgroundResource(imagesHere.getResourceId(i, 0));
				slideViewFlipper.addView(img);
			}
			
			runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.postDelayed(runnable, 3000);
					slideViewFlipper.showNext();
				}
			};
			
			handler  = new Handler();
			handler.postDelayed(runnable, 3000);
			popularList = (ListView)findViewById(R.id.home_list);

			popularList.setAdapter(new PopularListAdapter(CategoriesListScreen.this));
			
			popularList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					startActivity(new Intent(CategoriesListScreen.this, PersonsDescriptionView.class).putExtra("position", arg2).putParcelableArrayListExtra("popularItems", catDatarr).setAction("fromCat"));
				}
			});
	}
	
	
	class PopularListAdapter extends BaseAdapter{
    	
    	Context context;
    	public PopularListAdapter(Context context) {
    		super();
    		this.context=context;
        }

    	@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					LayoutInflater mInflater= LayoutInflater.from(context);
						convertView = mInflater.inflate(R.layout.popular_list_row_cat, null);
				}
				try {
					TextView personName = (TextView)convertView.findViewById(R.id.person_name);
					ImageView personImage = (ImageView)convertView.findViewById(R.id.popular_img);
					TextView personQuote = (TextView)convertView.findViewById(R.id.person_quote);
					personName.setText(""+catDatarr.get(position).getName());
					personQuote.setText(""+catDatarr.get(position).getQuote());
					personImage.setBackgroundResource(GreateSpeechesUtil.getResId(catDatarr.get(position).getImageId()+"_l", R.drawable.class));
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			 return convertView;
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return catDatarr.size();
		}


		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return catDatarr.get(position);
		}
		
	}
	
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:

	            		finish();
	                return true;

	         
	        }

	        return super.onOptionsItemSelected(item);
	    }

}
