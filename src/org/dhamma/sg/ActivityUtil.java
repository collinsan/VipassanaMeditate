package org.dhamma.sg;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

public class ActivityUtil {
	
	private static final String TAG = "ActivityUtil";
	private static int orientation;
	private static WakeLock wakeLock;
	private static String lockTag;
	

	/**
	 * Helper method to disableRotation and later use revertRotation to enable back rotation.
	 * Should be usable by 1 Activity or Fragment only and do not allow overlap as the original
	 * orientation is tracked in a static variable!
	 * 
	 * Sample usage:
	 * 
	 * in onCreate :
	 *       ActivityUtil.disableRotation(getActivity());
	 * 
	 * in onDestry :
	 *       ActivityUtil.revertRotation(getActivity());
	 * @param activity
	 * @return originalOrientation
	 */
	public static int disableRotation(Activity activity) {
		orientation = activity.getRequestedOrientation();
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		return orientation;
	}
	
	public static void revertRotation(Activity activity){
		// re-enable orientation
		activity.setRequestedOrientation(orientation);
	}


	/**
	 * @see disableRotation
	 * @param fragment
	 */
	
	public static void disableRotation(Fragment fragment) {
		disableRotation(fragment.getActivity());
		
	}
	
	public static void revertRotation(Fragment fragment){
		revertRotation(fragment.getActivity());
	}

	/**
	 * Helper method to turn on wakeLock for 1 lock.
	 * Must use wakeLockOff to revert it back.
	 * Not recommended to be used in complex situation. Preferably turn on and then off immediately
	 * after completion or at onDestroy
	 * @param view
	 */
	public static void wakeLockOn(Activity activity) {
		
		lockTag = activity.getTitle().toString();
		
		PowerManager mgr = (PowerManager)activity.getSystemService(Context.POWER_SERVICE);
		
		wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, lockTag);
		wakeLock.acquire();
		Log.d(TAG,"Wakelock [" + lockTag + "] acquired");
	}

	public static void wakeLockOff(Activity activity) {
		if ((null != wakeLock) && (wakeLock.isHeld())){
			try {
				wakeLock.release();
			} catch (Throwable th) {
				Log.d(TAG,"Wakelock exception ignored in release.");
			}
			
			Log.d(TAG,"Wakelock [" + lockTag + "] released");
		}		
	}

}
