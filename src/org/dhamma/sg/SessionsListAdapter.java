package org.dhamma.sg;

import java.util.List;

import org.dhamma.sg.model.Sessions;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zhenkongdao.common.util.TimeUtil;

public class SessionsListAdapter extends ArrayAdapter<Sessions> {

	private static final String TAG = "SessionsListAdapter";
	private Context context;
	private List<Sessions> sessionsList;

	  static class ViewHolder {
		    public TextView tvDateTime;
		    public TextView tvActivity;
		    public TextView tvDuration;
		    public TextView tvCompleted;
	  }	
	  
	public SessionsListAdapter(Context context, List<Sessions> sessionsList) {
		super(context, R.layout.diary_row, sessionsList);
		if (sessionsList == null)
			return;
		this.context = context;
		this.sessionsList = sessionsList;
		Log.d(TAG,"Initialized SessionsListAdapter with sessions : " + sessionsList.size());
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		if (null == rowView){
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			rowView = inflater.inflate(R.layout.diary_row, parent, false);
			
			//setup view holder for performance
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvDateTime = (TextView) rowView.findViewById(R.id.tv_array_datetime);
			viewHolder.tvActivity = (TextView) rowView.findViewById(R.id.tv_array_activity);
			viewHolder.tvDuration = (TextView) rowView.findViewById(R.id.tv_array_duration);
			viewHolder.tvCompleted = (TextView) rowView.findViewById(R.id.tv_array_completed);
			
			rowView.setTag(viewHolder);
			
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		Sessions thisSession = sessionsList.get(position);
		holder.tvDateTime.setText(DateUtil.formatDate(thisSession.getEntryDate()));
		holder.tvActivity.setText(thisSession.getEntry());
		holder.tvDuration.setText(TimeUtil.convertMilsToMin(thisSession.getDuration()) + "m");
		holder.tvCompleted.setText(getYN(thisSession.isCompleted()));
		
		
		//Log.d(TAG, "Initialised SessionsListAdapter.getView: " + position); // spam removed!
		
		return rowView;
	}

	private static String getYN(boolean isYes) {
		
		return (isYes ? "Y" : "N");
	}

}
