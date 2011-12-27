package com.t3hh4xx0r.gmusicsniper;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.content.Intent;


public class FirstStart extends Activity {
    TextView mContinue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      setContentView(R.layout.first_start);
                
      mContinue = (TextView) findViewById(R.id.continue_text);
      mContinue.setOnClickListener(mContinueListener);
      
      File f = new File(Constants.gMusicSniperDir);
      if (f.exists()) {
    	  onContinue();
      }
    }
    
    private OnClickListener mContinueListener = new OnClickListener() {
		public void onClick(View v) {
			onContinue();
		}
	};

	private void onContinue() {
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        intent.setClassName("com.t3hh4xx0r.gmusicsniper","com.t3hh4xx0r.gmusicsniper.GMusicSniperActivity");
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);
	}

}
