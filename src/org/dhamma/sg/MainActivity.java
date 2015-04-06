package org.dhamma.sg;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import org.dhamma.sg.model.Merits;
import org.dhamma.sg.model.Sessions;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.j256.ormlite.dao.Dao;

public class MainActivity extends ActionBarActivity {

	private static final String DB_URL = "vipasg_meditate.db";

	private static final String TAG = "VIPASG";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private Dao<Sessions, Integer> sessionDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// setup default values in Preferences
		PreferenceManager.setDefaultValues(getBaseContext(), R.xml.preferences, false);

		
		setContentView(R.layout.activity_main);

		// handling DB
		ArrayList<Class> daoList = new ArrayList<Class>();
		daoList.add(Sessions.class);
		daoList.add(Merits.class);
		
		DBUtil dbUtil = new DBUtil(getApplicationContext(), DB_URL, daoList);
		
		// test
		try {
			sessionDao = dbUtil.getDao(Sessions.class);
			
			long count = sessionDao.countOf();
			
			Log.d(TAG,"Found [" + count + "] entries in sessionDao");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//Toast.makeText(this, "onOptionsItemSelected clicked.", Toast.LENGTH_SHORT).show(); 
		int id = item.getItemId();
		
		// TODO: Help and statistics menu
		
		/**
		if (id == R.id.action_settings) {
		    Intent intentSetPref = new Intent(getApplicationContext(), MeritsPrefActivity.class);
		    startActivity(intentSetPref);		
			return true;
		}**/

		switch (id) {
		case R.id.action_settings:
		    Intent intentSetPref = new Intent(getApplicationContext(), PrefActivity.class);
		    startActivity(intentSetPref);		
		    //invalidateDiary();
			return true;
		case R.id.about:
			Intent intentAbout = new Intent(getApplicationContext(), AboutActivity.class);
			startActivity(intentAbout);
			return true;
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		//TODO: This is not being called!
	    invalidateDiary();
		super.onOptionsMenuClosed(menu);
	}
	
	private void invalidateDiary() {
		DiaryFragment diaryFrag = (DiaryFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":1");
		diaryFrag.invalidate();
		Log.d(TAG,"DiaryFrag invalidated : ["+ diaryFrag.getId() + "] " + diaryFrag);
		
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch(position){
			case 0 : return TimerFragment.newInstance(sessionDao);
			case 1 : return DiaryFragment.newInstance(sessionDao);
			case 2 : return ParamisFragment.newInstance();
			case 3 : return MiscFragment.newInstance();
			}
			
			//return PlaceholderFragment.newInstance(position + 1);
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
	}

}
