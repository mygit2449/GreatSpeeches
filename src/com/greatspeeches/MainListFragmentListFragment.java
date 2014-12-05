package com.greatspeeches;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.greatspeeches.categories.CategoriesListScreen;
import com.greatspeeches.helper.GreateSpeechesUtil;
import com.greatspeeches.helper.ScrollTabHolderFragment;
import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.slides.PersonsDescriptionView;

public class MainListFragmentListFragment extends ScrollTabHolderFragment implements OnScrollListener {

	private static final String ARG_POSITION = "position";

	private ListView mListView;

	private int mPosition;
//	public   ArrayList<HomeDataModel> homeDataarr = null;
//	private  List<String> categoriesList = null;

	public static Fragment newInstance(int position,  ArrayList<HomeDataModel> homeDataarr1) {
		MainListFragmentListFragment f = new MainListFragmentListFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}
	
	public static Fragment newInstance(int position,  List<String> categoriesList1) {
		MainListFragmentListFragment f = new MainListFragmentListFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(ARG_POSITION);
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
//		if(mPosition == 0){
//			homeDataarr =  ((MainActivity) getActivity()).homeDataarr;
//		}else{
//			categoriesList = ((MainActivity) getActivity()).categoriesList;
//		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, null);

		mListView = (ListView) v.findViewById(R.id.listView);
		View placeHolderView = inflater.inflate(R.layout.view_header_placeholder, mListView, false);
		placeHolderView.setClickable(false);
		mListView.addHeaderView(placeHolderView);
		mListView.setAdapter(new PopularListAdapter(getActivity(), mPosition));
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2 > 0){
					if (mPosition == 0) {
						startActivity(new Intent(getActivity(), PersonsDescriptionView.class).putExtra("position", arg2-1).putParcelableArrayListExtra("popularItems", ((MainActivity) getActivity()).homeDataarr).setAction("fromPop").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					}else {
						startActivity(new Intent(getActivity(), CategoriesListScreen.class).putExtra("categoryType", ((MainActivity) getActivity()).categoriesList.get(arg2-1)).setAction("fromCat").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//						getActivity().finish();
					}
				}
			}
		});
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListView.setOnScrollListener(this);
		mListView.setAdapter(new PopularListAdapter(getActivity(), mPosition));
	}

	@Override
	public void adjustScroll(int scrollHeight) {
		if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
			return;
		}

		mListView.setSelectionFromTop(1, scrollHeight);

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mScrollTabHolder != null)
			mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// nothing
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
					personName.setText(""+((MainActivity) getActivity()).homeDataarr.get(position).getName());
					personQuote.setText(""+((MainActivity) getActivity()).homeDataarr.get(position).getQuote());
					personImage.setBackgroundResource(GreateSpeechesUtil.getResId(((MainActivity) getActivity()).homeDataarr.get(position).getImageId()+"_l", R.drawable.class));
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else{
				TextView categoryName = (TextView)convertView.findViewById(android.R.id.text1);
				categoryName.setText(""+((MainActivity) getActivity()).categoriesList.get(position));
				categoryName.setTextColor(Color.parseColor("#ffffff"));
				categoryName.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
			}
			
			 
			 return convertView;
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			int count;
			if (type == 0) {
				count = ((MainActivity) getActivity()).homeDataarr.size();
			}else{
				count = ((MainActivity) getActivity()).categoriesList.size();
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
				item = ((MainActivity) getActivity()).homeDataarr.get(position);
			}else{
				item = ((MainActivity) getActivity()).categoriesList.get(position);
			}
			return item;
		}
		
	}
	

}