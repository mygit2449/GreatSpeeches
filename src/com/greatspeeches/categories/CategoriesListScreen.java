package com.greatspeeches.categories;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

import com.greatspeeches.HomeScreen;
import com.greatspeeches.R;
import com.greatspeeches.helper.GreateSpeechesUtil;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.slides.PersonsDescriptionView;
import com.greatspeeches.util.DataParser;

public class CategoriesListScreen extends FragmentActivity {

	private TypedArray imagesHere = null;
	private String typeStr = "";

    private ViewFlipper slideViewFlipper;
    private Handler handler;
    private Runnable runnable; 
    public  ArrayList<HomeDataModel> catDatarr = null;
    private ListView popularList;
    public static Typeface arimoype,alextype;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories_list_screen);
		
		typeStr = getIntent().getStringExtra("categoryType");

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(typeStr);
		
		if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category1))){
			catDatarr = new DataParser(CategoriesListScreen.this).parser("science.xml");
			imagesHere = getResources().obtainTypedArray(R.array.science);
		}else if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category2))){
			catDatarr = new DataParser(CategoriesListScreen.this).parser("sports.xml");
			imagesHere = getResources().obtainTypedArray(R.array.sports);
		}else if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category3))){
			catDatarr = new DataParser(CategoriesListScreen.this).parser("cultural.xml");
			imagesHere = getResources().obtainTypedArray(R.array.cultural);

		}else if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category4))){
			catDatarr = new DataParser(CategoriesListScreen.this).parser("politicians.xml");
			imagesHere = getResources().obtainTypedArray(R.array.politicians);
		}else if(typeStr.equalsIgnoreCase(""+getResources().getString(R.string.category5))){
			catDatarr = new DataParser(CategoriesListScreen.this).parser("womens.xml");
			imagesHere = getResources().obtainTypedArray(R.array.womens);
		}
		
		
		slideViewFlipper = (ViewFlipper)findViewById(R.id.flipper);
		
		alextype = Typeface.createFromAsset(getAssets(),"AlexBrush-Regular.ttf"); 
		arimoype = Typeface.createFromAsset(getAssets(),"Arimo-Regular.ttf");
		
		
		 for (int i = 0; i < imagesHere.length(); i++) {
				ImageView img = new ImageView(this);
				img.setBackground(imagesHere.getDrawable(i));
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
					startActivity(new Intent(CategoriesListScreen.this, PersonsDescriptionView.class).putExtra("position", arg2).putParcelableArrayListExtra("popularItems", catDatarr));
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
					personName.setTypeface(alextype);
					personQuote.setTypeface(arimoype);
					personQuote.setText(""+catDatarr.get(position).getQuote());
					personImage.setBackgroundResource(GreateSpeechesUtil.getResId(catDatarr.get(position).getImageId(), R.drawable.class));
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.categories_list_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
            // Navigate "up" the demo structure to the launchpad activity.
            // See http://developer.android.com/design/patterns/navigation.html for more.
            startActivity(new Intent(CategoriesListScreen.this, HomeScreen.class).setAction("fromCat"));
            finish();
            return true;

		}
		return super.onOptionsItemSelected(item);
	}
}
