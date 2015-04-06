package org.dhamma.sg;

import java.sql.SQLException;

import org.dhamma.sg.NumberPickerFragment.NumberPickerListener;
import org.dhamma.sg.model.Sessions;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.zhenkongdao.common.util.TimeUtil;


public class TimerFragment extends Fragment implements NumberPickerListener{

	protected static final CharSequence START = "Start";
	protected static final CharSequence RESUME = "Resume";
	protected static final CharSequence STOP = "Stop";
	protected static final CharSequence PAUSE = "Pause";
	private static final long MILS_1s = 1000;
	private static final long MILS_1m = 60*MILS_1s;
	private static final long MILS_1H = 60*MILS_1m;
	protected static final String TAG = "VIPASG_Timer";
	protected static final String MEDITATE_VIPA = "Meditate Vipassana";
	private static final String PREF_SOUND_ON = "pref_sound_on";
	private static final String PREF_WAVE_ON = "pref_wave_on";
	private static final String PREF_CHANTING_ON = "pref_chanting_on";
	
	protected enum STATE {
		STOP, STARTED, PAUSE, RESUMED
	}

	private TextView mTimerCounter;
	private CountDownTimer timer;
	protected static WakeLock wakeLock;
	protected static int orientation;
	protected static long lastDuration;
	private static View rootView;
	
	/** 
	 * The ms in time counter that mpStart will have ended
	 */
	private static long mpStartEndMil;
	/**
	 * The milisecond time that mpStop media should start
	 */
	private static int mpStopStartMil;
	private static MediaPlayer mpStop;
	private static MediaPlayer mpWave;
	private static MediaPlayer mpStart;
	private static long counter = 60 * MILS_1m;
	private static Dao<Sessions, Integer> sessionDao;	
	
	private Sessions mSession;
	private Button mButton;
	private static STATE mState = STATE.STOP;

	public static Fragment newInstance(Dao<Sessions,Integer> dao) {
		
		TimerFragment fragment = new TimerFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		
		sessionDao = dao;
		return fragment;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
				
		// setup member variables to view control
		
		mButton = (Button) rootView.findViewById(R.id.buttonStart);
		
		mTimerCounter = (TextView) rootView.findViewById(R.id.textTimerCounter);
		
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/digital-7.ttf");
		if (tf == null){
			Log.e(TAG, "digital-7.ttf is not found!");
		}
		else {
			mTimerCounter.setTypeface(tf);
		}
		
		mTimerCounter.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Counter pressed!");
				Log.i(TAG,"execute is " + getFragmentManager().executePendingTransactions());
				Log.i(TAG,"FragmentManager : " + getFragmentManager().findFragmentByTag(getTag()));

				NumberPickerFragment nbpFragment = new NumberPickerFragment(TimerFragment.this);
				nbpFragment.show(getFragmentManager(), getTag());
				Log.i(TAG,"nbpFragment.FragmentManager : " + nbpFragment.getFragmentManager().findFragmentByTag(getTag()));
			}
		});
			
		// handles after rotation here

		// resetup state of button to match mState static var
		Log.d(TAG,"newInstance mState : " + mState);
		setState(mState);

		// setup audio and timer. Also setup counter after a rotation/resume
		setupTimer(counter);
		if ((mState == STATE.STARTED) || (mState == STATE.RESUMED))
			startTimer();

		// if we just rotated and resume, start media
		// Actually media didn't stop. no need
		
		Log.i(TAG,"Fragment Tag [" + getTag() + "] id [" + getId() + "]");
		Log.i(TAG,"FragmentManager : " + getFragmentManager().findFragmentByTag(getTag()));
		
		
		//Log.i(TAG,"commit is " + getFragmentManager().beginTransaction().add(R.id.pager,this).commit());

		return rootView;
	}
	
	@Override
	public void onStart() {
		// disable orientation
		//Log.d(TAG,"onStart. Disable Rotation");
		//ActivityUtil.disableRotation(this);

		super.onStart();
	}

	@Override
	public void onPause() {
		//Log.d(TAG,"onPause. Revert rotation");
		//ActivityUtil.revertRotation(this);
		super.onPause();
	}
	

	private void pauseMedia() {
		if (mpWave.isPlaying())
			mpWave.pause();
		
		// check and pause mpStart if needed
		if (mpStart.isPlaying())
			mpStart.pause();
		
		if (mpStop.isPlaying())
			mpStop.pause();
	
		ActivityUtil.wakeLockOff(getActivity());
	}

	private void startMedia() {
		ActivityUtil.wakeLockOn(getActivity());
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		if (!pref.getBoolean(PREF_SOUND_ON,true)){
			Log.d(TAG,"SOUND is off returning");
			return;
		}
		
		// start the wave in the background in a loop
		Log.d(TAG,"pref_wave_on : " + pref.getBoolean(PREF_WAVE_ON, true));
		
		if (pref.getBoolean(PREF_WAVE_ON, true)){
			int vol = pref.getInt("pref_wave_vol", 100);
			mpWave.setVolume( calcVolume(vol), calcVolume(vol));
			mpWave.start();
		}
		
		if (!pref.getBoolean(PREF_CHANTING_ON, true)){
			Log.d(TAG,"Not turning on chanting! pref_chanting_on : " + pref.getBoolean(PREF_CHANTING_ON, true));
			return;
		}
		// chantings below
		
		// start the startup speech if counter is still within the start speech time
		int vol = pref.getInt("pref_chanting_vol", 100);
		if (counter > mpStartEndMil){
			mpStart.setVolume( calcVolume(vol), calcVolume(vol));
			mpStart.start();
			Log.d(TAG,"mpStop started!");
		}
		
		// check if we should start mpStop
		if (counter < mpStopStartMil ){
			mpStop.setVolume( calcVolume(vol), calcVolume(vol));
			mpStop.start();
			Log.d(TAG,"mpStop resumed!");
		}					

	}
	
	private void stopMedia(){
		
		if ((mpWave != null) && (mpWave.isPlaying()))
			mpWave.stop();
		
		if ((mpStart != null) && (mpStart.isPlaying()))
			mpStart.stop();
		
		if ((mpStop != null) && (mpStop.isPlaying()))
			mpStop.stop();
		
		ActivityUtil.wakeLockOff(getActivity());
		
	}

	private void startTimer() {
		Log.d(TAG,"StartTimer : " + counter);
		
		timer = createTimer( counter );
		timer.start();
	}

	private void setupTimer(long duration) {
		
		setupMedia();

		// setup the counter tracking when mpStart will have finished playing
		mpStartEndMil = duration - mpStart.getDuration();

		// setup counter to duration
		counter = duration;
		 
		// sync the counter display too
		mTimerCounter.setText(TimeUtil.formatTime(duration));

		// setup the button triggers
		mButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// check toggle
				//CharSequence buttonStr = ((TextView)v).getText();
				if (mState == STATE.STOP){
					setState(STATE.STARTED);
					
					// record the session starts
					
					try {
						mSession = new Sessions();
						mSession.setEntry(MEDITATE_VIPA);
						mSession.setDuration(counter);
						mSession.setCompleted(false);
						mSession.setEntryDate(DateUtil.getNow());

						sessionDao.create(mSession);
						Log.d(TAG, "Created session : " + mSession);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					invalidateDiary();
					
					startMedia();
					startTimer();
				}
				else if(mState == STATE.STARTED){
					setState(STATE.PAUSE);
					
					pauseMedia();
					timer.cancel();
				}
				else if(mState == STATE.PAUSE){
					setState(STATE.RESUMED);
					startMedia();
					startTimer();
				}
				else if(mState == STATE.RESUMED){
					setState(STATE.PAUSE);
					pauseMedia();
					timer.cancel();
				}
				// STOP not needed!
				/*
				else {
					Log.d(TAG, "Stop Button clicked!");
					mp.stop();
					timer.cancel();
					((TextView)v).setText(START);
				}*/
				
			}
		});		
	}

	
	private void setupMedia() {
		// setup mediaplayers on first init.
		// Check they are not null first, because after rotation, setup will be called again
		//final MediaPlayer mp = MediaPlayer.create(rootView.getContext(),R.raw.audio_1hr_eng_cn);
		if (mpWave == null){
			mpWave = MediaPlayer.create(rootView.getContext(),R.raw.beach_65min);
			mpWave.setLooping(true);
			//lower the wave volume
			Log.i(TAG,"Lower wave volume to " + calcVolume(70));
			mpWave.setVolume( calcVolume(70), calcVolume(70));
		}

		if (mpStart == null)
			mpStart = MediaPlayer.create(rootView.getContext(),R.raw.meditation_start);
		
		if (mpStop == null)
			mpStop = MediaPlayer.create(rootView.getContext(),R.raw.meditation_end);
		
		// setup when we start mpStop to the nearest sec
		mpStopStartMil = (int) (mpStop.getDuration() - (mpStop.getDuration() % MILS_1s) + MILS_1s);
		Log.d(TAG,"mpStopStartMil : " + mpStopStartMil);
		
	}

	protected void setState(STATE newState) {
		Log.d(TAG, "SetState : " + newState.toString());
		CharSequence stateStr;

		switch (newState) {
		case STARTED:
			// started state, next state allowed is PAUSE
			stateStr = PAUSE;
			break;
		case PAUSE:
			stateStr = RESUME;
			break;
		case RESUMED:
			stateStr = PAUSE;
			break;
		case STOP:
			stateStr = START;
			break;
		default:
			Log.e(TAG, "Unknown state in setState : " + newState);
			return;
		}

		mButton.setText(stateStr);
		mState = newState;

	}

	/**
	 * Volume in Media is in log. This method helps to calc volume by passing the desired volume
	 * in percent (e.g. half is 50, and silent is 0)
	 * @param percent
	 * @return volume in log
	 */
	private float calcVolume(int percent) {
		return (1.0f - (float)(Math.log(100-percent)/Math.log(100)));
	}

	private void invalidateDiary() {
		//TODO: testing
		DiaryFragment diaryFrag = (DiaryFragment) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":1");
		diaryFrag.invalidate();
		Log.d(TAG,"DiaryFrag invalidated : ["+ diaryFrag.getId() + "] " + diaryFrag);
	}

	private CountDownTimer createTimer(long duration) {

		lastDuration = duration;
		
		CountDownTimer cdt = new CountDownTimer(duration , MILS_1s) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				mTimerCounter.setText( TimeUtil.formatTime(millisUntilFinished));
				counter = millisUntilFinished;
				
				// check if we should start mpStop

				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

				if ((pref.getBoolean(PREF_CHANTING_ON, true)) && (counter < mpStopStartMil) 
						&& (counter > mpStopStartMil - 1000)) {
					Log.d(TAG,"mpStopStartMil[" + mpStopStartMil + "]. Counter [" + counter + "]. mpStop Started!");
					
					int vol = pref.getInt("pref_chanting_vol", 100);
					mpStop.setVolume( calcVolume(vol), calcVolume(vol));
					mpStop.start();
				}
			}
			
			@Override
			public void onFinish() {
				Log.d(TAG,"Count down completed!");
				mpWave.stop();
				
				// re-enable orientation
				//getActivity().setRequestedOrientation(orientation);				
				
				counter = lastDuration;
				mTimerCounter.setText( TimeUtil.formatTime(counter));
				setState(STATE.STOP);
				
				// save session
				
				if (sessionDao !=null){
					mSession.setCompleted(true);
					try {
						sessionDao.update(mSession);
						Log.d(TAG, "Updated session to completed!\n" + mSession);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				//need to notify DiaryFragment when completed to reload the activities
				invalidateDiary();
				
				ActivityUtil.wakeLockOff(getActivity());
			}
		};
		
		return cdt;
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "Destroyed! Stopping Media!");
		//stopMedia();
		
		super.onDestroy();
	}

	@Override
	public void onNumberPickerOK(int val) {
		Log.d(TAG, "onNumberPickerOK : " + val);
		setupTimer(val*MILS_1m);
	}
	
}


