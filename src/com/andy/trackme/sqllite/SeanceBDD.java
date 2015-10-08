package com.andy.trackme.sqllite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.andy.trackme.entities.Seance;

public class SeanceBDD {
	private static final int VERSION_BDD = 1;
	private static final String NAME_BDD = "seance.db";
	 private String[] allColumns = { DBHelper.ID_SEANCE,DBHelper.DUREE_SEANCE,DBHelper.DISTANCE_SEANCE
			 , DBHelper.VITESSE_SEANCE,DBHelper.CALORIES_SEANCE, DBHelper.DATE_SEANCE};
	private SQLiteDatabase bdd;
	private ContentValues values;
	private DBHelper dbHelper;
	
	
	public SeanceBDD(Context context) {
		super();
		dbHelper = new DBHelper(context, NAME_BDD, null, VERSION_BDD);
	}
	
	public void open(){
		//TODO Open Data Base
		dbHelper.getWritableDatabase();
	}
	
	public void close(){
		//TODO Close Data Base
		dbHelper.close();
	}
	
	public SQLiteDatabase getBDD(){
		return bdd;
	}
	
	public long insertTop(Seance seance){
		Log.e("erreur p", "00");
		bdd = dbHelper.getReadableDatabase();
		Log.e("erreur p", "000");
		Cursor cursor = bdd.rawQuery("select max("+DBHelper.ID_SEANCE+") from "+DBHelper.TABLE_SEANCE, null);
		Log.e("erreur p", "100");
		if (cursor.moveToFirst()) { seance.setId(cursor.getInt(0)+1);}
		else {seance.setId(1);}
		//TODO Add Article to data base
		Log.e("erreur p", "10");
		values = new ContentValues();
		Log.e("erreur p", "11");
		values.put(DBHelper.ID_SEANCE, seance.getId());
		Log.e("erreur p", "12");
		values.put(DBHelper.DUREE_SEANCE, seance.getDuree());
		Log.e("erreur p", "13");
		values.put(DBHelper.DISTANCE_SEANCE, seance.getDistance());
		Log.e("erreur p", "14");
		values.put(DBHelper.VITESSE_SEANCE, seance.getVitesse());
		Log.e("erreur p", "15");
		values.put(DBHelper.CALORIES_SEANCE, seance.getCalories());
		Log.e("erreur p", "16");
		values.put(DBHelper.DATE_SEANCE, seance.getDateSeance());
		Log.e("erreur p", "17");
		bdd.insert(DBHelper.TABLE_SEANCE, null, values);
		Log.e("erreur p", "18");
		return 0;
	}
	
	
	public int removeAllSeances(){
		//TODO Remove all Table
		bdd.delete(DBHelper.TABLE_SEANCE, null, null);
		return 0;
	}
	
	public int removeSeance(int index){
		//TODO Remove one Article
	    bdd.delete(DBHelper.TABLE_SEANCE, DBHelper.ID_SEANCE
	        + " = " + index, null);
		return 0;
	}
	
	public List<Seance> selectAll() {
	      List<Seance> list = new ArrayList<Seance>();
	      //TODO Get list of Article
	      bdd = dbHelper.getReadableDatabase();
	      Cursor cursor = bdd.query(DBHelper.TABLE_SEANCE,
	    	        allColumns, null, null, null, null, null);

	    	    cursor.moveToFirst();
	    	    while (!cursor.isAfterLast()) {
	    	    	Seance seance = new Seance();
	    	    	seance.setId(cursor.getInt(0));
	    	    	seance.setDuree(cursor.getInt(1));
	    	    	seance.setDistance(cursor.getInt(2));
	    	    	seance.setVitesse(cursor.getString(3));
	    	    	seance.setCalories(cursor.getInt(4));
	    	    	seance.setDateSeance(cursor.getString(5));
	    	      list.add(seance);
	    	      cursor.moveToNext();
	    	    }
	    	    // make sure to close the cursor
	    	    cursor.close();
	      return list;
	   }
	
	/*
	public Seance getSeanceById(int index,List<Seance> listSeance){
		Seance seance = listSeance.get(index);
		return seance;
	}
	*/
}
	
