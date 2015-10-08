package com.andy.trackme.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	public static final String TABLE_SEANCE= "seance";
	
	public static final String ID_SEANCE = "id";
	public static final String DUREE_SEANCE = "duree";
	public static final String DISTANCE_SEANCE = "distance";
	public static final String VITESSE_SEANCE = "vitesse";
	public static final String CALORIES_SEANCE = "calories";
	public static final String DATE_SEANCE = "date";
	private static final String CREATE_SEANCE = "CREATE TABLE " + TABLE_SEANCE + " ("+ 
			ID_SEANCE + " INTEGER, "+
			DUREE_SEANCE + " INTEGER, "+
			DISTANCE_SEANCE + " INTEGER, "+
			VITESSE_SEANCE + " INTEGER, "+
			CALORIES_SEANCE + " INTEGER, "+
			DATE_SEANCE + " TEXT);";
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_SEANCE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEANCE);
		 
	        // Create tables again
	        onCreate(db);
	}

}
