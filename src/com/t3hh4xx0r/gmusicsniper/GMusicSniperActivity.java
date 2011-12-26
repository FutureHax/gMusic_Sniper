package com.t3hh4xx0r.gmusicsniper;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
	Button mCopyDBButton;
	static boolean mIsCacheAvailable = false;
    boolean mUnChecked = true;
	static boolean mUsesDir1 = false;
	static boolean mUsesDir2 = false;
	static boolean mHasBB = false;
	TextView mStatusText;
	TextView mTopText;
	TextView mWarningText;
	String gMusicCacheDir = null;
	String gMusicDBDir = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mStartButton = (Button) findViewById(R.id.start_button);
        mStartButton.setOnClickListener (mStartButtonListener);
        mExecuteButton = (Button) findViewById(R.id.execute_button);
        mExecuteButton.setOnClickListener (mExecuteButtonListener);
        mCopyDBButton = (Button) findViewById(R.id.copy_db_button);
        mCopyDBButton.setOnClickListener(mCopyDBButtonListener);
        mStatusText = (TextView) findViewById(R.id.status_text);
        mWarningText = (TextView) findViewById(R.id.warning_text);
        mTopText = (TextView) findViewById(R.id.top_text);
        
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
    
    private OnClickListener mCopyDBButtonListener = new OnClickListener() {
		public void onClick(View v) {
			try {
				copyDB();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		File f1 = new File(Constants.gCacheDir1);
		File f2 = new File(Constants.gCacheDir2);
		
		if (f1.exists()) {
			mUsesDir1 = true;
			gMusicCacheDir = (Constants.gCacheDir1);
			gMusicDBDir = (Constants.gMusicDB1);
		} else if (f2.exists()) {
			mUsesDir2 = true;
			gMusicCacheDir = (Constants.gCacheDir2);
			gMusicDBDir = (Constants.gMusicDB2);
		}
		if (mUsesDir1 || mUsesDir2) {
			mIsCacheAvailable = true;
			mStatusText.setText("Cached music found!");	
			mExecuteButton.setVisibility(View.VISIBLE);
			mStartButton.setVisibility(View.INVISIBLE);
			mWarningText.setVisibility(View.VISIBLE);
		} else {
			mIsCacheAvailable = false;
			mStatusText.setText("No cached music found.");
		}
	}
	
	public void makeAvailable() {
		File f = new File(Constants.gMusicSniperDir + "music/");
		if (!f.exists()) {
			String message = "Creating directories.";
			makeToast(message);
			f.mkdir();
			f.mkdirs();
			if (f.exists()) {
				makeAvailable();
			}
		} else {
			File dir = new File(gMusicCacheDir);
			String[] songNames = dir.list();
			File[] songs = dir.listFiles();
			for (int i=0; i<songs.length; i++) {
				File song = songs[i];
				String songName = songNames[i];
				String[] bits = songName.split("/");
				String songStripped = bits[bits.length-1];
				File output = new File(Constants.gMusicSniperDir + "music/" + songStripped);
				try {
					copyFile(song, output);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			File shitFile = new File(Constants.gMusicSniperDir + "music/" + ".nomedia");
			if (shitFile.exists()) {
				shitFile.delete();
			}
			
			mWarningText.setVisibility(View.GONE);
			mTopText.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			mTopText.setText(R.string.finish);
			mExecuteButton.setVisibility(View.INVISIBLE);
			
			checkBB();
		}
	}

	private void checkBB() {
		File BB1 = new File(Constants.BBPath1);
		File BB2 = new File(Constants.BBPath2);
		
		if (BB1.exists() || BB2.exists()) {
			mHasBB = true;
		}

		if (mHasBB) {
			mStatusText.setText(R.string.busybox);
			mCopyDBButton.setVisibility(View.VISIBLE);
		} else {
			mStatusText.setText(R.string.no_busybox);
		}
	}

	private void copyDB() throws IOException {
		String[] commands = {"busybox cp " + gMusicDBDir + Constants.musicDB + " " + Constants.gMusicSniperDir + Constants.musicDB + " \n"};
		RunAsRoot(commands);
		File DBFile = new File (Constants.gMusicSniperDir + Constants.musicDB);
		if (DBFile.exists()) {
			getId3();
		}
	}
	
    public void RunAsRoot(String[] cmds) throws IOException{
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());            
        for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd+"\n");
        }           
        os.writeBytes("exit\n");  
        os.flush();
    }
	
    public void getId3() {
    	File dir = new File(Constants.gMusicSniperDir + "music/");
    	String[] songNames = dir.list();
    	File[] songs = dir.listFiles();
    	for (int i=0; i<songs.length; i++) {
    		String songName = songNames[i];
    		String songFinal = songName.substring(0, songName.lastIndexOf('.'));
    		try {
    			getTrackName(songFinal);
    		} catch (Exception e) {
    			goneWrong();
				e.printStackTrace();
    		}
    	}
	}
    
    private void getTrackName(String songFinal) {
    	int songFinalValue = Integer.parseInt(songFinal);
        DBAdapter db = new DBAdapter(this);
        String message = String.valueOf(songFinalValue);
        //makeToast(message);
        
        //---get a title---
        db.open();
        Cursor c = db.getTitle(songFinalValue);
        if (c.moveToFirst())        
            DisplayTitle(c);
        else
            Toast.makeText(this, "No title found", 
            		Toast.LENGTH_LONG).show();
        db.close();
	}

    public void DisplayTitle(Cursor c) {
    	String message = ("id: " + c.getString(0) + "\n" +
    	        "TITLE: " + c.getString(12) + "\n");
		Toast.makeText(this,message,Toast.LENGTH_LONG).show();
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
	
	public void goneWrong() {
		mStatusText.setText(R.string.wrong_status);
		mTopText.setText(R.string.wrong_top);
		mExecuteButton.setVisibility(View.INVISIBLE);
		mStartButton.setVisibility(View.INVISIBLE);
		mCopyDBButton.setVisibility(View.INVISIBLE);
	}
}