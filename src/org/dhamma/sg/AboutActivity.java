package org.dhamma.sg;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
		Spanned htmlContent = Html.fromHtml(getString(R.string.html_about_content));
		setHtmlTextValue(R.id.about_content,htmlContent);
		
	}
	
	private void setHtmlTextValue(int resId, Spanned htmlContent) {
		TextView textView = (TextView) findViewById (resId);
		textView.setMovementMethod (LinkMovementMethod.getInstance());
		textView.setText(htmlContent);
	}

	
}
