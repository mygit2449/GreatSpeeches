package com.greatspeeches.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.greatspeeches.models.HomeDataModel;
import com.greatspeeches.models.Quotes;

public class GreatLivesDataBaseHelper extends SQLiteOpenHelper{
	
	/* Database name */
	private final static String DATABASE_NAME = "greatlives.sqlite";
	
	public final static String SELECT_QUERY = "select * from %s;";	
	
	private final static int DATA_VERSION = 1;
	
	private String DB_PATH; 
	
	private Context mContext;
	private SQLiteDatabase mSqLiteDatabase = null;

	
	public GreatLivesDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATA_VERSION);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		DB_PATH="/data/data/" + context.getPackageName() + "/databases/";
        Log.v("The Database Path" , DB_PATH);
	}

	/* create an empty database if data base is not existed */
	public void createDataBase() throws IOException{
			 
	    	boolean dbExist = checkDataBase();
	 
	    	if(dbExist){
	    		//do nothing - database already exist
	    	}else{
	 
	    		//By calling this method and empty database will be created into the default system path
	               //of your application so we are gonna be able to overwrite that database with our database.
	        	//this.getReadableDatabase();
	 
	        	try {
	    			copyDataBase();
	    			
	        	}
	        	catch(SQLiteException ex){
	        			ex.printStackTrace();
	        		throw new Error("Error copying database");
	        	
	        	} catch (IOException e) {
	        		e.printStackTrace();
	        		throw new Error("Error copying database");
	        	}
	    		
	    		try{
	    			openDataBase();
	    		}
	    		catch(SQLiteException ex){
	    			ex.printStackTrace();
	    			throw new Error("Error opening database");
	    			
	    		}catch(Exception e){
	    			e.printStackTrace();
	    			throw new Error("Error opening database");
	    		}
	    		
	    	}
	    
	}
	
	
	/* checking the data base is existed or not */
	/* return true if existed else return false */
	private boolean checkDataBase()
	{
		try
		{
		   String check_Path = DB_PATH  + DATABASE_NAME;
		   mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		return mSqLiteDatabase != null ? true : false;
	}
	
	 private void copyDataBase() throws IOException{
	    	
	    	File dbDir = new File(DB_PATH);
	        if (!dbDir.exists()) {
	            dbDir.mkdir();
	            
	        }
	    	//Open your local db as the input stream
	    	Log.v("1 Opening the assest folder db " , mContext.getAssets() + "/"+ DATABASE_NAME);
	    	InputStream myInput = mContext.getAssets().open(DATABASE_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DATABASE_NAME;
	 
	    	Log.i("2 Copy the db to the path " , outFileName);
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	    	//transfer bytes from the inputfile to the outputfile
	    	Log.i("Open the output db " , outFileName);
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	    	Log.i("Copied the database file" , outFileName);
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 
	    }
	 
	/* Open the database */
	public void openDataBase() throws SQLException{
		
		String check_Path = DB_PATH  + DATABASE_NAME;
		if(mSqLiteDatabase!=null)
    	{        	
			mSqLiteDatabase.close();
			mSqLiteDatabase = null;
			mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);    		
    	}else{        
    		mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);        
    	}
	}
	
	public void closeDataBase(){
		
		if (mSqLiteDatabase != null) {
			mSqLiteDatabase.close();
			mSqLiteDatabase = null;
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public Cursor select(String query){
		return mSqLiteDatabase.rawQuery(query, null);
	}
	
	
	public ArrayList<HomeDataModel>   getData(String type)
	{
		ArrayList<HomeDataModel> mTotalRecords = new ArrayList<HomeDataModel>();
		Cursor mDataCursor = null;
		HomeDataModel mHomeModel =  null;
		if (type.contains("'")) {
			type = type.replace("'", "''");
		}
		try {
			mDataCursor = queryDataBase("select id,name,img,quote,info,audio,video,category,bdate,ddate,achievements from persons where category = '"+type+"'");
			if (mDataCursor.getCount() > 0) {
				int iCtr=0;
				mDataCursor.moveToPosition(iCtr);
				do{
					mHomeModel = new HomeDataModel();
					mHomeModel.setId(mDataCursor.getString(0));
					mHomeModel.setName(mDataCursor.getString(1));
					mHomeModel.setImageId(mDataCursor.getString(2));
					mHomeModel.setQuote(mDataCursor.getString(3));
					mHomeModel.setInfo(mDataCursor.getString(4));
					mHomeModel.setAudio(mDataCursor.getString(5));
					mHomeModel.setVideourl(mDataCursor.getString(6));
					mHomeModel.setType(mDataCursor.getString(7));
					mHomeModel.setbDate(mDataCursor.getString(8));
					mHomeModel.setdDate(mDataCursor.getString(9));
					mHomeModel.setAchievement(mDataCursor.getString(10));
					mTotalRecords.add(mHomeModel);
					mHomeModel = null;
					iCtr++;
				} while (mDataCursor.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(null != mDataCursor)
				mDataCursor.close();
		}
		return mTotalRecords;
	}
	
	public ArrayList<Quotes> getQuotes(String personId){
		ArrayList<Quotes> quotesList = new ArrayList<Quotes>();
		Quotes mQuotes =null;
		Cursor quotesCursor = null;
		try {
			quotesCursor = queryDataBase("select personName, quote_msg from quotes where person_id  = '"+personId+"'");
			if (quotesCursor.getCount() > 0) {
				int iCtr=0;
				quotesCursor.moveToPosition(iCtr);
				do{
					mQuotes = new Quotes();
					mQuotes.setqPerson(quotesCursor.getString(0));
					mQuotes.setqMessage(quotesCursor.getString(1));
					quotesList.add(mQuotes);
					mQuotes = null;
					iCtr++;
				} while (quotesCursor.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			if (quotesCursor != null) {
				quotesCursor.close();
			}
		}
		return quotesList;
	}
	
	
	public Cursor queryDataBase(String query){
		GreatLivesDataBaseHelper mDataBaseHelper = new GreatLivesDataBaseHelper(mContext);
		mSqLiteDatabase = mDataBaseHelper.getReadableDatabase();
		Log.v("query ", query);
		Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
		return cursor;
	}
	
	
	@Override
    public synchronized void close() {
        if(mSqLiteDatabase != null){
        	mSqLiteDatabase.close();
        super.close();
        }   
    }

}
