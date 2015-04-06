package org.dhamma.sg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerFragment extends DialogFragment{

    public interface NumberPickerListener {
        public void onNumberPickerOK(int val);
    }

	protected static final String TAG = "NBP";

	private NumberPickerListener mListener;

	private NumberPicker nbp;

	private View rootView;

	/**
	 * Usage : 
	 *     NumberPickerFragment nbpFragment = new NumberPickerFragment(MyFragement.this);
	 * @param listener
	 */
    public NumberPickerFragment(NumberPickerListener listener) {
    	
    	
        // Verify that the host activity implements the callback interface
        try {
        	mListener = listener;
        	
        	// all these tried and does not work! Todo: remove when done!
            // Instantiate the NoticeDialogListener so we can send events to the host
            //mListener = (NumberPickerListener) activity.getFragmentManager().findFragmentById(R.id.pager);
        	// android:switcher:2131230780:0
        	// do nothing! Setup in constructor!
        	//mListener = (NumberPickerListener) activity.getFragmentManager().findFragmentByTag("android:switcher:2131230780:0");
        	Log.d(TAG,"mListener: " + mListener);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(
                    "Caller [" + mListener + "] must implement NumberPickerListener");
        }    	
	}


	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG,"onCreateDialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    rootView = inflater.inflate(R.layout.numberpicker, null, false);
	    
	    builder.setView(rootView);
	    
	    nbp = (NumberPicker) rootView.findViewById(R.id.npdNumber);
	    nbp.setMinValue(30);
	    nbp.setMaxValue(24*60);
	    nbp.setValue(60);
	    
	    
	    // Add action buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   Log.d(TAG, "onClick PositiveButton");
	            	   
	            	   int val = nbp.getValue();
	            	   mListener.onNumberPickerOK(val);
	            	   
	               }
	           })
	           .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   Log.d(TAG, "onClick NegativeButton");
	                  
	               }
	           });      
	    return builder.create();
	}
}
