package org.dhamma.sg;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


/**
 * A DB util based on OrmLiteSqlite
 * Usage example:
 * 
 * 		DBUtil dbUtil = new DBUtil(getApplicationContext(), DB_URL, daoList);
 *	
 *		try {
 *			myDao = dbUtil.getDao(MyDao.class);
 *		} catch (SQLException e) {
 *			e.printStackTrace();
 *		}
 *		
 * @author Collin Ng
 *
 */
public class DBUtil extends OrmLiteSqliteOpenHelper{

	private static final int DB_VERSION = 2;
	private static final String TAG = "DBUtil";
	private String dbName;
	@SuppressWarnings("rawtypes")
	private ArrayList<Class> daoList;

	public DBUtil(Context context, String dbUrl,
			@SuppressWarnings("rawtypes") ArrayList<Class> daoList) {
		super(context, dbUrl, null, DB_VERSION);
		
		// store dbName to override problem with getDatabaseName() requiring API level 14
		dbName = dbUrl;
		// store the list of daos we manage
		this.daoList = daoList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		Log.i(TAG, "onCreate : " + dbName);
		
		try {
			for(int i=0;i<daoList.size();i++){
				Class clazz = daoList.get(i);
				TableUtils.createTable(connectionSource, clazz);
				Log.i(TAG,"Created table for [" + clazz + "]");
			}
		} catch (SQLException e) {
			Log.e(TAG,"SQLException creating db : " + dbName);
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onUpgrade(SQLiteDatabase database,
			  ConnectionSource connectionSource, int oldVersion, int newVersion) {
		/*
		try {
			for(int i=0;i<daoList.size();i++){
				Class clazz = daoList.get(i);
				TableUtils.dropTable(connectionSource, clazz,true);
				TableUtils.createTable(connectionSource, clazz);
				
				Log.i(TAG,"Created table for [" + clazz + "]");
			}
		} catch (SQLException e) {
			Log.e(TAG,"SQLException creating db : " + dbName);
			e.printStackTrace();
			throw new RuntimeException(e);
		}*/				
	}

	// Static generic helper queries beyond here
	
	public static long getCount(Dao<?, ?> dao) throws SQLException {
		
		return dao.queryBuilder().countOf();
	}

}
