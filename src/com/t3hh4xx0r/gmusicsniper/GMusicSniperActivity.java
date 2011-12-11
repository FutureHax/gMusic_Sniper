package com.t3hh4xx0r.gmusicsniper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;



public class GMusicSniperActivity extends Activity {
	Button mStartButton;
	Button mExecuteButton;
	static boolean mIsCacheAvailable = false;
	static boolean mUnChecked = true;
	TextView mStatusText;
	TextView mWarningText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mStartButton = (Button) findViewById(R.id.start_button);
        mStartButton.setOnClickListener (mStartButtonListener);
        mExecuteButton = (Button) findViewById(R.id.execute_button);
        mExecuteButton.setOnClickListener (mExecuteButtonListener);
        mStatusText = (TextView) findViewById(R.id.status_text);
        mWarningText = (TextView) findViewById(R.id.warning_text);
        
        if (!hasStorage(true)) {
        	mStatusText.setText("SDCard is needed for this app to function.");
        	mStartButton.setEnabled(false);
        } else {
        	if (mUnChecked) {
        		mStatusText.setText("Untested.");
        	} else {
        		checkCache();
        	}
        }
    }
    
    private OnClickListener mExecuteButtonListener = new OnClickListener() {
		public void onClick(View v) {
			makeAvailable();
		}
    };
    
    private OnClickListener mStartButtonListener = new OnClickListener() {
		public void onClick(View v) {
			checkCache();
		}
	};

	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	private void checkCache() {
		mUnChecked = false;
		File f = new File(Constants.gCacheDir);
		if (!f.exists()) {
			mIsCacheAvailable = false;
			mStatusText.setText("No cached music found.");
		} else {
			mIsCacheAvailable = true;
			mStatusText.setText("Cached music found!");	
			mExecuteButton.setVisibility(View.VISIBLE);
			mStartButton.setEnabled(false);
			mWarningText.setVisibility(View.VISIBLE);
		}
	}
	
	public void makeAvailable() {
		File f = new File(Constants.gMusicSniperDir);
		if (!f.exists()) {
			String message = "Directory not available";
			makeToast(message);
			f.mkdir();
			f.mkdirs();
			if (f.exists()) {
				makeAvailable();
			}
		} else {
			File dir = new File(Constants.gCacheDir);
			String[] songNames = dir.list();
			File[] songs = dir.listFiles();
			for (int i=0; i<songs.length; i++) {
				File song = songs[i];
				String songName = songNames[i];
				String[] bits = songName.split("/");
				String songStripped = bits[bits.length-1];
				File output = new File(Constants.gMusicSniperDir + songStripped);
				Log.d("tag", songName);
				try {
					copyFile(song, output);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			File shitFile = new File(Constants.gMusicSniperDir + ".nomedia");
			if (shitFile.exists()) {
				shitFile.delete();
			}
			mWarningText.setText(R.string.finish);
			mExecuteButton.setEnabled(false);
		}
	}

	private void copyFile(File sourceFile, File destFile)
            throws IOException {

		FileChannel source = null;
		FileChannel destination = null;
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		if (destination != null && source != null) {
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null) {
            source.close();
		}
		if (destination != null) {
            destination.close();
		}
	}
	
	static public boolean hasStorage(boolean requireWriteAccess) {		
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        if (requireWriteAccess) {
	            return true;
	        } else {
	            return false;
	        }
	    } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
}