package org.dhamma.sg;

import java.sql.SQLException;
import java.util.List;

import org.dhamma.sg.model.Sessions;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.zhenkongdao.common.util.TimeUtil;

public class DiaryFragment extends Fragment{

	private static final String TAG = "DiaryFragment";
	private static final String PREF_SESSION_SHOW_INCOMPLETE = "pref_session_show_incomplete";
	private static final Long MAX_ROW = (long) 200;
	private static Dao<Sessions, Integer> sessionDao;
	private ListView listEntries;
	private SessionsListAdapter sessionListAdapter;
	private List<Sessions> sessionsList;
	private TextView tvCompletedSessions;
	private TextView tvCompletedTime;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_diary, container,
				false);
		
		listEntries = (ListView) rootView.findViewById(R.id.listEntries);
		// MeritsArrayAdapter(rootView.getContext(), meritsList, secDate, dbHelper, true)
		
		//ArrayList<String> entries = new ArrayList<String>();
		
			//sessionsList = sessionDao.queryForAll( );
			//sessionsList = sessionDao.queryBuilder().orderBy(Sessions.ENTRYDATE_FIELD, false).query();
			
			/*
			List<String> entries = new ArrayList<String>();
			entries.add("Date      Entry         Duration    Completed");
			for(int i=0;i<sList.size();i++){
				Sessions ent = sList.get(i);
				
				entries.add(DateUtil.formatDate( ent.getEntryDate() ) + " " + 
						ent.getEntry().substring(0,8) + " " + 
						TimeUtil.convertMilsToMin(ent.getDuration()) + " " +
						( ent.isCompleted() ? "Y" : "N" ) );
			}
			//ArrayAdapter<String> ad = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, entries);
			*/
		
			tvCompletedSessions = (TextView) rootView.findViewById(R.id.tv_completed_sessions);
			tvCompletedTime = (TextView) rootView.findViewById(R.id.tv_completed_time);
		
			sessionsList = querySessions();
			sessionListAdapter = new SessionsListAdapter(rootView.getContext(), sessionsList);
			invalidate();
			
			listEntries.setAdapter(sessionListAdapter);
			sessionListAdapter.notifyDataSetChanged();
			Log.d(TAG,"Updated listview with [" + sessionsList.size() + "] entries!");


		
		return rootView;
	}

	public static Fragment newInstance(Dao<Sessions, Integer> dao) {
		DiaryFragment fragment = new DiaryFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		
		sessionDao = dao;
		return fragment;
	}

	public void invalidate() {
		Log.d(TAG,"invalidate!");
		
		
		sessionsList = querySessions();
		sessionListAdapter.clear();
		sessionListAdapter.addAll(sessionsList);
		sessionListAdapter.notifyDataSetChanged();
		
		queryUpdateStats();
	}

	/**
	 * Query and update stats for completed sessions, completed time and etc...
	 */
	private void queryUpdateStats() {

		try {
			//QueryBuilder<Sessions, Integer> qb = sessionDao.queryBuilder();
			//qb.where().eq(Sessions.COMPLETED_FIELD, true);
			//long completedSessions = qb.countOf();

			GenericRawResults<Object[]> rawResults =
					sessionDao.queryRaw( 
					    "select count(*),sum(duration) from sessions where completed=1", 
					    new DataType[] { DataType.LONG, DataType.LONG });

			Long completedSessionsHr;
			Long completedSessionsL;
			for (Object[] resultArray : rawResults) {
				completedSessionsHr = (Long) resultArray[1];
				completedSessionsL = (Long) resultArray[0];
				
				
				tvCompletedSessions.setText(completedSessionsL.toString());
				tvCompletedTime.setText(TimeUtil.formatTimeHrMin(completedSessionsHr));
				Log.d(TAG,"Completed Sessions [" + completedSessionsL + "] hours [" + completedSessionsHr + "]");
				
			}
			

			rawResults.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}

	private List<Sessions> querySessions() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		boolean prefShowIncompleted;
		prefShowIncompleted = pref.getBoolean(PREF_SESSION_SHOW_INCOMPLETE,true);
		
		Log.d(TAG,PREF_SESSION_SHOW_INCOMPLETE + " : " + prefShowIncompleted);
		
		try {
			QueryBuilder<Sessions, Integer> qb = sessionDao.queryBuilder();

			if (!prefShowIncompleted)
				qb.where().eq(Sessions.COMPLETED_FIELD, true);
			qb.orderBy(Sessions.ENTRYDATE_FIELD, false);
			qb.limit(MAX_ROW);
			
			sessionsList = qb.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sessionsList;
	}
}
