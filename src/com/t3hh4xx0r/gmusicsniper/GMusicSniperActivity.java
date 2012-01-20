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
import org.blinkenlights.jid3.v2.APICID3V2Frame;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class GMusicSniperActivity extends Activity {
	Button mStartButton;
	Button mExecuteButton;
	Button mCopyDBButton;
	Button mGetID3Button;	
	Button mCleanButton;	
	static boolean mUsesDir1a = false;
	static boolean mUsesDir1b = false;
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
	String gMusic = null;
	String gMusicDBDir = null;
	ProgressBar mProgressBar;
    byte[] art;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mCacheStatusText = (TextView) findViewById(R.id.cache_status);
        mDBStatusText = (TextView) findViewById(R.id.db_status);
        mCopiedStatusText = (TextView) findViewById(R.id.copied_status);
        mCopiedStatusImage = (ImageView) findViewById(R.id.copied_status_image);
        mSDStatusText = (TextView) findViewById(R.id.sd_status);
        mSDStatusImage = (ImageView) findViewById(R.id.sd_status_image);
        mCacheStatusImage = (ImageView) findViewById(R.id.cache_status_image);
        mDBStatusImage = (ImageView) findViewById(R.id.db_status_image);
        mExecuteButton = (Button) findViewById(R.id.execute_button);
        mExecuteButton.setOnClickListener (mExecuteButtonListener);
        mCopyDBButton = (Button) findViewById(R.id.copy_db_button);
        mCopyDBButton.setOnClickListener(mCopyDBButtonListener);
        mGetID3Button = (Button) findViewById(R.id.get_id3_button);
        mGetID3Button.setOnClickListener(mGetID3ButtonListener);
        mCleanButton = (Button) findViewById(R.id.clean_button);
        mCleanButton.setOnClickListener(mCleanButtonListener);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

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
    	checkDB();
    	checkCopied();
    	checkSD();
    }
    private OnClickListener mExecuteButtonListener = new OnClickListener() {
		public void onClick(View v) {
    		mProgressBar.setVisibility(View.VISIBLE);
			makeAvailable();
		}
    };
    
    private OnClickListener mCopyDBButtonListener = new OnClickListener() {
 		public void onClick(View v) {
 			copyDB();
 		}
     };
     
    private OnClickListener mCleanButtonListener = new OnClickListener() {
 		public void onClick(View v) {
 			cleanCache();
 		}
     };
    
    private OnClickListener mGetID3ButtonListener = new OnClickListener() {
		public void onClick(View v) {
    		mProgressBar.setVisibility(View.VISIBLE);
			getId3();
		}
    };

	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	private void checkCache() {
		File f1a = new File(Constants.gCacheDir1a);
		File f1b = new File(Constants.gCacheDir1b);
		File f2 = new File(Constants.gCacheDir2);
		
		if (f1a.exists()) {
			mUsesDir1a = true;
			gMusicDBDir = (Constants.gMusicDB1);
			gMusic = (Constants.gCacheDir1a);
		} else if (f1b.exists()) {
			mUsesDir1b = true;
			gMusicDBDir = (Constants.gMusicDB1);
			gMusic = (Constants.gCacheDir1b);
		} else if (f2.exists()) {
			mUsesDir2 = true;
			gMusicDBDir = (Constants.gMusicDB2);
			gMusic = (Constants.gCacheDir2);
		}
		
		if (mUsesDir1a || mUsesDir1b || mUsesDir2) {
			mCacheStatusText.setText("Offline Music Found!");
			mCacheStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
		} else {
			mCacheStatusText.setText("No Offline Music Found!");
			mExecuteButton.setEnabled(false);
			mCleanButton.setEnabled(false);
		}
	}
	
	public void checkSD() {
        if (!hasStorage(true)) {
        	mSDStatusText.setText("SD Not Available!");
        	mExecuteButton.setEnabled(false);
        	mCopyDBButton.setEnabled(false);
        	mCleanButton.setEnabled(false);
        } else {
			mSDStatusText.setText("SD Available!");
			mSDStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
        }
	}
	
	public void cleanCache() {
		File dir = new File(gMusic);
		File[] songs = dir.listFiles();
		for (int i=0;songs.length>i;i++) {
			File song = songs[i];
			song.delete();
		}
		dir.delete();
		restart();
	}
	
	public void checkCopied() {
		File directory = new File(Constants.gMusicSniperDir + "music/");
		File[] contents = directory.listFiles();
		if (contents != null && contents.length != 0) {
			mCopiedStatusText.setText("Music Sniped!");
			mCopiedStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
        	mGetID3Button.setEnabled(true);
		} else {
			mCopiedStatusText.setText("No Music Sniped!");
        	mGetID3Button.setEnabled(false);
		}
	}
		
	final Handler mHandler = new Handler(){ 
        public void handleMessage (Message  msg) {
        	switch (msg.what) {
        	case 0:
        		mProgressBar.setVisibility(View.INVISIBLE);
        		setupViews();
        	break;
        	
        	case 1:
        		mCacheStatusImage.setImageResource(R.drawable.btn_check_off_focused_holo_dark);
        	    mCacheStatusText.setText("Reboot before continuing!");
        		mProgressBar.setVisibility(View.INVISIBLE);
        		setupViews();
        	break;
        	
        	case 2:
        		restart();
        	}
        }
	}; 

	public void makeAvailable() {
		
		new Thread(new Runnable() {
            public void run() {
            	copyAll();
                mHandler.sendEmptyMessage(0); 
            }
        }).start();
        
		File shitFile = new File(Constants.gMusicSniperDir + "music/" + ".nomedia");
		if (shitFile.exists()) {
			shitFile.delete();
		}
	}
	
	public void copyAll() {
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

		String RootWarning = "Your device may not be rooted. Root is currently required for this app to function";
		alertDialog.setMessage(RootWarning);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	  mHandler.sendEmptyMessage(2); 
		      } 
		});
		
		String[] commands = {"cat " + gMusicDBDir + Constants.musicDB + " > " + Constants.gMusicSniperDir + Constants.musicDB + " \n"};
		try {
			RunAsRoot(commands);
		} catch (IOException e) {
			alertDialog.show();
		}
        mHandler.sendEmptyMessage(2); 
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
		new Thread(new Runnable() {
            public void run() {
            	getInfos();
                mHandler.sendEmptyMessage(0); 
            }
        }).start();
    }
    
    public void getInfos() {

    	File dir = new File(Constants.gMusicSniperDir + "music/");
    	String[] songNames = dir.list();
    	File[] songs = dir.listFiles();
        DBAdapter db = new DBAdapter(this);
        db.open();
    	for (int i=0; i<songs.length; i++) {
    		String songName = songNames[i];
    		if (songName.endsWith("mp3")) {
    			String songFinal = songName.substring(0, songName.lastIndexOf('.'));
    			try {
    		        Cursor c = db.getTitle(Integer.parseInt(songFinal));
    				editTracks(c, songFinal, db);
    		        c.close();
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
        db.close();
        mHandler.sendEmptyMessage(1); 
	}

    public void editTracks (Cursor c, String songFinal, DBAdapter db) throws IOException, ID3Exception {
    	
        Log.d("SNIPER", c.getString(0) + " " + c.getString(1) + " " + c.getString(2) + " " + c.getString(3));

    	File song = new File(Constants.gMusicSniperDir + "music/" + songFinal + ".mp3");
    	File output = new File(Constants.gMusicSniperDir + "music/" + c.getString(1) + "/" + c.getString(2) + "/" + c.getString(0) + ".mp3");
    	File outputDir = new File(Constants.gMusicSniperDir + "music/" + c.getString(1) + "/" + c.getString(2) + "/");
    	if (!outputDir.isDirectory()) {
    		outputDir.mkdirs();
    	}

    	Cursor a = db.getArtwork(c.getString(3));
    	File file = new File(a.getString(0));
    	a.close();

    	byte[] b = new byte[(int) file.length()];
    	try {
    	FileInputStream fileInputStream = new FileInputStream(file);
    	fileInputStream.read(b);
    	}
    	catch (IOException e1) {
    	e1.printStackTrace();
    	}
    	
        // create an MP3File object representing our chosen file
        MediaFile oMediaFile = new MP3File(song);

        // create a v2.3.0 tag object, and set values using convenience methods
        ID3V2_3_0Tag tag = new ID3V2_3_0Tag();
        tag.setArtist(c.getString(1));  // sets TPE1 frame
        tag.setAlbum(c.getString(2));  // sets TALB frame
        tag.setTitle(c.getString(0));  // sets TIT2 frame
        APICID3V2Frame newFrontCover = new APICID3V2Frame("image/jpeg",APICID3V2Frame.PictureType.FrontCover,"Album Cover",b);
        tag.addAPICFrame(newFrontCover);
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