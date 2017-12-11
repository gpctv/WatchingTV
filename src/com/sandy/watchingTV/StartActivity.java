package com.sandy.watchingTV;

import com.sandy.watchingTV.R;

import action.TitleAsyncTask;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StartActivity extends Activity {
	private ProgressBar PB1;
	private TextView tvMessage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); //ß‚ActionBar™∫title¡Ù¬√
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		System.out.println("Start Creat");
		tvMessage=(TextView) findViewById(R.id.textMessage);
		PB1=(ProgressBar) findViewById(R.id.progressBar1);
		PB1.setVisibility(View.GONE);
		
		new TitleAsyncTask(this, PB1,tvMessage).execute("http://tv.atmovies.com.tw/tv/attv.cfm?action=todaytime");
	}
}
