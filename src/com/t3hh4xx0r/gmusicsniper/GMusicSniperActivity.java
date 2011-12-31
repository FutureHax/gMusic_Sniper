package com.t3hh4xx0r.gmusicsniper;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class GMusicSniperActivity extends Activity {
	Button mStartButton;
	Button mExecuteButton;
	Button mCopyDBButton;
	Button mGetID3Button;	
	static boolean mUsesDir1 = false;
	static boolean mUsesDir2 = false;
	static boolean mHasBB = false;
	TextView mTopText;
	TextView mCopiedStatusText;
	TextView mCacheStatusText;
	TextView mBBStatusText;
	TextView mSDStatusText;
	TextView mDBStatusText;
	ImageView mCopiedStatusImage;
	ImageView mCacheStatusImage;
	ImageView mDBStatusImage;
	ImageView mSDStatusImage;
	ImageView mBBStatusImage;
	String gMusic = null;
	String gMusicDBDir = null;
	String BBPath = null;


	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mCacheStatusText = (TextView) findViewById(R.id.cache_status);
        mBBStatusText = (TextView) findViewById(R.id.bb_status);
        mDBStatusText = (TextView) findViewById(R.id.db_status);
        mCopiedStatusText = (TextView) findViewById(R.id.copied_status);
        mCopiedStatusImage = (ImageView) findViewById(R.id.copied_status_image);
        mSDStatusText = (TextView) findViewById(R.id.sd_status);
        mSDStatusImage = (ImageView) findViewById(R.id.sd_status_image);
        mCacheStatusImage = (ImageView) findViewById(R.id.cache_status_image);
        mBBStatusImage = (ImageView) findViewById(R.id.bb_status_image);
        mDBStatusImage = (ImageView) findViewById(R.id.db_status_image);
        mExecuteButton = (Button) findViewById(R.id.execute_button);
        mExecuteButton.setOnClickListener (mExecuteButtonListener);
        mCopyDBButton = (Button) findViewById(R.id.copy_db_button);
        mCopyDBButton.setOnClickListener(mCopyDBButtonListener);
        mGetID3Button = (Button) findViewById(R.id.get_id3_button);
        mGetID3Button.setOnClickListener(mGetID3ButtonListener);

        setupViews();
    }
    
    public void setupViews() {
		File f = new File(Constants.gMusicSniperDir + "music/");
		if (!f.exists()) {
			String message = "Creating directories.";
			makeToast(message);
			f.mkdir();
			f.mkdirs();
		}
    	checkCache();
    	checkBB();
    	checkDB();
    	checkCopied();
    	checkSD();
    }
    private OnClickListener mExecuteButtonListener = new OnClickListener() {
		public void onClick(View v) {
			makeAvailable();
		}
    };
    
    private OnClickListener mCopyDBButtonListener = new OnClickListener() {
		public void onClick(View v) {
			copyDB();
		}
    };
    
    private OnClickListener mGetID3ButtonListener = new OnClickListener() {
		public void onClick(View v) {
			getId3();
		}
    };

	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	private void checkCache() {
		File f1 = new File(Constants.gCacheDir1);
		File f2 = new File(Constants.gCacheDir2);
		
		if (f1.exists()) {
			mUsesDir1 = true;
			gMusicDBDir = (Constants.gMusicDB1);
			gMusic = (Constants.gCacheDir1);
		} else if (f2.exists()) {
			mUsesDir2 = true;
			gMusicDBDir = (Constants.gMusicDB2);
			gMusic = (Constants.gCacheDir2);
		}
		if (mUsesDir1 || mUsesDir2) {
			mCacheStatusText.setText("Offline Music Found!");
			mCacheStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
		} else {
			mCacheStatusText.setText("No Offline Music Found!");
			mExecuteButton.setEnabled(false);
		}
	}
	
	public void checkSD() {
        if (!hasStorage(true)) {
        	mSDStatusText.setText("SD Not Available!");
        	mExecuteButton.setEnabled(false);
        	mCopyDBButton.setEnabled(false);
        } else {
			mSDStatusText.setText("SD Available!");
			mSDStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
        }
	}
	
	public void checkCopied() {
		File directory = new File(Constants.gMusicSniperDir + "music/");
		File[] contents = directory.listFiles();
		if (contents != null && contents.length != 0) {
			mCopiedStatusText.setText("Music Sniped!");
			mCopiedStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
		} else {
			mCopiedStatusText.setText("No Music Sniped!");
        	mGetID3Button.setEnabled(false);
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
			File dir = new File(gMusic);
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
		}
		restart();
	}

	private void checkBB() {
		File BB1 = new File(Constants.BBPath1);
		File BB2 = new File(Constants.BBPath2);
		
		if (BB1.exists()) {
			BBPath = (Constants.BBPath1);
			mHasBB = true;
		} else if (BB2.exists()) {
			BBPath = (Constants.BBPath2);
			mHasBB = true;
		} else {
			mHasBB = false;
		}

		if (mHasBB) {
			mBBStatusText.setText("Busybox found at " + BBPath);
			mBBStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);

		} else {
			mBBStatusText.setText("Busybox not found!");
		}
	}

	private void checkDB() {
		File DBFile = new File (Constants.gMusicSniperDir + Constants.musicDB);
		if (!DBFile.exists()) {
			mDBStatusText.setText("Database file not found!");
        	mGetID3Button.setEnabled(false);
		} else {
			mDBStatusText.setText("Database file found!");
			mDBStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
		}
	}
	
	private void copyDB() {
		AlertDialog alertDialog = new AlertDialog.Builder(GMusicSniperActivity.this).create();
		alertDialog.setTitle("Sorry");

		String BBWarning = "Your device is not rooted and/or does not have busybox installed. \n" +
							"You will need to manually copy the music database. This can be done with any file manager. \n\n" +
							"Please copy the file from \n" +
							"\"/data/data/com.google.android.music/databases/music.db\" or\n"+
							"\"/data/data/com.android.music/databases/music.db\" to\n" +
							"\"/t3hh4xx0r/gMusicSniper/music.db\"\n" +
							"and re launch the app.";
		alertDialog.setMessage(BBWarning);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		  		restart();
		      } 
		});
		
		if (mHasBB) {
			String[] commands = {"busybox cp " + gMusicDBDir + Constants.musicDB + " " + Constants.gMusicSniperDir + Constants.musicDB + " \n"};
			try {
				RunAsRoot(commands);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			restart();
		} else {
			alertDialog.show();
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
    		if (songName.endsWith("mp3")) {
    			String songFinal = songName.substring(0, songName.lastIndexOf('.'));
    			try {
    				getTrackName(songFinal);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
        mCacheStatusImage.setImageResource(R.drawable.btn_check_off_focused_holo_dark);
        mCacheStatusText.setText("Reboot before continuing!");
	}
    
    private void getTrackName(String songFinal) throws ID3Exception {
    	int songFinalValue = Integer.parseInt(songFinal);
        DBAdapter db = new DBAdapter(this);
        db.open();
        //---get a title---
        Cursor c = db.getTitle(songFinalValue);
        c.getString(0);
        c.getString(1);
        c.getString(2);
        try {
			editTracks(c, songFinal);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        db.close();
	}

    public void editTracks (Cursor c, String songFinal) throws IOException, ID3Exception{
    	File song = new File(Constants.gMusicSniperDir + "music/" + songFinal + ".mp3");
    	File output = new File(Constants.gMusicSniperDir + "music/" + c.getString(1) + "/" + c.getString(2) + "/" + c.getString(0) + ".mp3");
    	File outputDir = new File(Constants.gMusicSniperDir + "music/" + c.getString(1) + "/" + c.getString(2) + "/");
    	if (!outputDir.isDirectory()) {
    		outputDir.mkdirs();
    	}

        // create an MP3File object representing our chosen file
        MediaFile oMediaFile = new MP3File(song);

        // create a v2.3.0 tag object, and set values using convenience methods
        ID3V2_3_0Tag tag = new ID3V2_3_0Tag();
        tag.setArtist(c.getString(1));  // sets TPE1 frame
        tag.setAlbum(c.getString(2));  // sets TALB frame
        tag.setTitle(c.getString(0));  // sets TIT2 frame

        // set this v2.3.0 tag in the media file object
        oMediaFile.setID3Tag(tag);
       
        // update the actual file to reflect the current state of our object 
        oMediaFile.sync();
        
    	copyFile(song, output);
    	song.delete();
    	
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
	
	private void restart() {
        finish();
        Intent intent = new Intent(GMusicSniperActivity.this, GMusicSniperActivity.class);
        startActivity(intent);
	}
}