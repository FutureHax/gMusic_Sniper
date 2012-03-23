package com.t3hh4xx0r.cloudsniper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v2.APICID3V2Frame;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class UIActivity extends Activity {
	Button mAllButton;
	Button mSelectButton;
	Button mStop;
	
	public boolean running = false;
	static boolean mUsesDir1 = false;
	static boolean mUsesDir2 = false;
	boolean hasExtras = false;
	boolean showing;
	
	TextView mpath;
	TextView mCounter;
	TextView mTrackText;
	TextView mCacheStatusText;
	TextView mSDStatusText;
    TextView artist_t;
    TextView album_t;
    TextView mCount;
    
	Bitmap artworkage;

	ImageView mCacheStatusImage;
	ImageView mSDStatusImage;
	ImageView album_i;
	
	ProgressBar mProgressBar;
    ContentResolver cR;
    Cursor c;
    
    String gMusic = null;
	String gDira = null;
	String gDirb = null;
    String _id = "unknown";
    String album_id = "unknown";
    String album = "unknown";
    String albumArtist = "unknown";
    String composer = "unknown";
    String _data = "unknown";
    String track = "unknown";
    String title = "unknown";
    String genre = "unknown";
    String year = "unknown";    
    String size = "unknown";
    String discCount = "unknown";
    String discNumber = "unknown";
    String artistSort = "unknown";
    String canonicalAlbum = "unknown";
    String canonicalName = "unknown";
	String l_album = "shit";
	static String[] songIds;
	String[] contents;
	String[] IDS;
	String[] infos;

    ArrayList<String> ids;
    public static ArrayList<String> titles;
    public static ArrayList<String> albums;
    public static ArrayList<String> artists;
    public static ArrayList<Bitmap> artwork;
    
    RelativeLayout Snipe;
    LinearLayout Menu;

	int current;
	int count;
    
    Notification bg_notification;
    NotificationManager mNotificationManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(
                getApplicationContext().NOTIFICATION_SERVICE);	
        if (showing) {
   		 showing = false;
		 }
		 mNotificationManager.cancel(42);
		 
		 try {
            Bundle extras = getIntent().getExtras();
            IDS = extras.getStringArray("tracks");
            hasExtras = true;
        } catch (Exception e) {}
        
        cR = getContentResolver();

        mStop = (Button) findViewById(R.id.stop_b);
        mStop.setOnClickListener(mStopListener);
        mAllButton = (Button) findViewById(R.id.all_button);
        mAllButton.setOnClickListener (mAllButtonListener);
        if (hasExtras) {
        	mAllButton.setText("Snipe selected tracks.");
        }
        mSelectButton = (Button) findViewById(R.id.select_button);
        mSelectButton.setOnClickListener (mSelectButtonListener);
 
        album_t = (TextView) findViewById(R.id.album_t);
        mCount = (TextView) findViewById(R.id.count);
        mCounter = (TextView) findViewById(R.id.counter);
        artist_t = (TextView) findViewById(R.id.artist_t);
        mTrackText = (TextView) findViewById(R.id.trackText);
        mpath = (TextView) findViewById(R.id.path);
        mCacheStatusText = (TextView) findViewById(R.id.cache_status);
        mSDStatusText = (TextView) findViewById(R.id.sd_status);
        
        album_i = (ImageView) findViewById(R.id.album_i);
        mSDStatusImage = (ImageView) findViewById(R.id.sd_status_image);
        mCacheStatusImage = (ImageView) findViewById(R.id.cache_status_image);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        
        ids = new ArrayList<String>();
        artists = new ArrayList<String>();
        titles = new ArrayList<String>();
        albums = new ArrayList<String>();
        artwork = new ArrayList<Bitmap>();
 
        Snipe = (RelativeLayout) findViewById(R.id.snipe);
        Menu = (LinearLayout) findViewById(R.id.menu_prefs);
        
        File File = new File(Constants.gMusicSniperDir + "/music/" + ".nomedia");
		if (File.exists()) {
			File.delete();
		}
		setupViews();
		
    }
    
    private OnClickListener mSelectButtonListener = new OnClickListener() {
  		public void onClick(View v) {
  	        Intent intent = new Intent(UIActivity.this, SelectionActivity.class);
  	        startActivity(intent);
  		}
      };		
       
     private OnClickListener mAllButtonListener = new OnClickListener() {
 		public void onClick(View v) {
 	        ImportTask task = new ImportTask();
 	        running = true;
            
 	        if (!hasExtras) {
 	        	task.execute(songIds);
 	        } else {
 	        	if (IDS.length != 0) {
 	        		task.execute(IDS);
 	        	}
 	        }
 	        mProgressBar.setVisibility(View.VISIBLE);
 		}
     };
     
	 private OnClickListener mStopListener = new OnClickListener() {
	   		public void onClick(View v) {
	   			running = false;
	   			Snipe.setVisibility(View.GONE);
	   			Menu.setVisibility(View.GONE);
	   			mProgressBar.setVisibility(View.VISIBLE);
	   		}
	 };
	       
     public void setupViews() {
    	mpath.setText(Constants.gMusicSniperDir);
		File f = new File(Constants.gMusicSniperDir + "/music/");
		if (!f.exists()) {
			makeToast("Creating directories.");
			f.mkdir();
			f.mkdirs();
		}
    	checkCache();
    	checkSD();
    	copyAssets();
    }
    
	private void checkCache() {
		File f1 = new File(Constants.gCacheDir1);
		File f2 = new File(Constants.gCacheDir2);
		if (f1.isDirectory()) {
			gDira = (Constants.gMusicDir1);
			gDirb = "/data/data/com.google.android.music/cache/artwork/";
			mUsesDir1 = true;
			gMusic = new String(Constants.gCacheDir1);
		} else if (f2.isDirectory()) {
			mUsesDir2 = true;
			gMusic = new String(Constants.gCacheDir2);
			gDira = (Constants.gMusicDir2);
			gDirb = "/data/data/com.android.music/cache/artwork/";
		}
		
		if (mUsesDir1 || mUsesDir2) {
			File f = new File(gMusic);
			File[] contents = f.listFiles();
			if (contents != null && contents.length > 1) {
				mCacheStatusText.setText("Offline Music Found!");
				mCacheStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
				setCounter();
			} else {
				mCacheStatusText.setTextColor((getResources().getColor(R.color.red)));
				mCacheStatusText.setText("No Offline Music Found!");
				mCount.setVisibility(View.GONE);
				mCacheStatusImage.setImageResource(R.drawable.btn_check_off_focused_holo_dark);
				mAllButton.setEnabled(false);
				mSelectButton.setEnabled(false);
			}
		} else {
			mCacheStatusText.setTextColor((getResources().getColor(R.color.red)));
			mCacheStatusText.setText("No Offline Music Found!");
			mCount.setVisibility(View.GONE);
			mCacheStatusImage.setImageResource(R.drawable.btn_check_off_focused_holo_dark);
			mAllButton.setEnabled(false);
		}
	}
	
	public void checkSD() {
        if (!hasStorage(true)) {
        	mSDStatusText.setTextColor((getResources().getColor(R.color.red)));
        	mSDStatusText.setText("SD Not Available!");
        	mAllButton.setEnabled(false);
        } else {
			mSDStatusText.setText("SD Available!");
			mSDStatusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
        }
	}
	
	public void copyAssets() {
	    AssetManager assetManager = getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list("");
	    } catch (IOException e) {}
	    for(String filename : files) {
	    	if (filename.equals("default_art.jpg")) {
	    		InputStream in = null;
	    		OutputStream out = null;
	    		try {
	    			in = assetManager.open(filename);
	    			out = new FileOutputStream(Constants.gMusicSniperDir + "/" + filename);
	    			copyStream(in, out);
	    			in.close();
	    			in = null;
	    			out.flush();
	    			out.close();
	    			out = null;
	    		} catch(Exception e) {}
	    	}
	    }
	}
	
	public void setCounter() {
		File dir = new File(gMusic);
    	contents = dir.list();
    	for (int i=0;i<contents.length;i++) {
    		if (contents[i].endsWith("mp3")) {
    			ids.add(contents[i]);
    		}
    	}
    	songIds = ids.toArray(new String[ids.size()]);
    	if (hasExtras) {
    		count = IDS.length;
    	} else {
    		count = songIds.length;
    	}
    	mCount.setText(Integer.toString(count));
    }
    
	public class ImportTask extends AsyncTask<String, String, String> {
			       
		private Bitmap decodeBitmap(String f){
			File file = new File(f);
		    Bitmap b = null;
		    try {
		        //Decode image size
		        BitmapFactory.Options o = new BitmapFactory.Options();
		        o.inJustDecodeBounds = true;
		        o.inPurgeable=true;

		        FileInputStream fis = new FileInputStream(file);
		        BitmapFactory.decodeStream(fis, null, o);
		        fis.close();

		        int scale = 1;
		        if (o.outHeight > 600 || o.outWidth > 600) {
		            scale = (int)Math.pow(2, (int) Math.round(Math.log(250 / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
		        }

		        //Decode with inSampleSize
		        BitmapFactory.Options o2 = new BitmapFactory.Options();
		        o2.inSampleSize = scale;
		        o2.inPurgeable=true;
		        fis = new FileInputStream(file);
		        b = BitmapFactory.decodeStream(fis, null, o2);
		        fis.close();
		    } catch (IOException e) {
		    }
		    return b;
		}
		
		protected void onProgressUpdate(String... track) {			
	        current++;
			if (mProgressBar.isShown() && running) {
	    		mProgressBar.setVisibility(View.GONE);
			}
			if (!Snipe.isShown() && running) {
				Snipe.setVisibility(View.VISIBLE);
			}
			if (!l_album.equals(track[1])) {
				artworkage = decodeBitmap(track[3]);	        
			}
			artwork.add(artworkage);
			album_i.setImageBitmap(artworkage);
			titles.add(track[0]);
       		artists.add(track[2]);
       		albums.add(track[1]);
       		if (!track[0].equals("unknown")) {
       			mTrackText.setText(track[0]); 
       		} else {
	       		mTrackText.setText("Failed to import track title.");
	       	}
       		if (!track[1].equals("unknown")) {
	       		album_t.setText(track[1]);	       		
	       	} else {
	       		album_t.setText("Failed to import album name.");
	       	}
	       	if (!track[2].equals("unknown")) {
	       		artist_t.setText(track[2]);
	       	} else {
	       		artist_t.setText("Failed to import artist name.");
	       	}	    
    		mCounter.setText(Integer.toString(current)+"/"+Integer.toString(count));   
    		
            bg_notification.contentView.setTextViewText(R.id.status_text, track[2] + " - " + track[0]);
            bg_notification.contentView.setTextViewText(R.id.counter, Integer.toString(current)+"/"+Integer.toString(count));
	        bg_notification.contentView.setProgressBar(R.id.status_progress, count, current, false);
			if (Constants.showart) {
				if (!l_album.equals(track[1])) {
					bg_notification.contentView.setImageViewBitmap(R.id.status_icon, artworkage);
				}
			} else {
				bg_notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_launcher);
			}
       		l_album = track[1];
			if (showing && Constants.show) {
	        	mNotificationManager.notify(42, bg_notification);
	        } else {
	   		 mNotificationManager.cancel(42);	        	
	        }
	    }

		protected void onPreExecute() {						
	        Intent intent = new Intent();
	        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

	        bg_notification = new Notification(R.drawable.ic_launcher, "Sniping", System
	                .currentTimeMillis());
	        bg_notification.flags = bg_notification.flags | Notification.FLAG_ONGOING_EVENT;
	        bg_notification.contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
	        bg_notification.contentIntent = pendingIntent;
	        bg_notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_launcher);
	        bg_notification.contentView.setProgressBar(R.id.status_progress, count, current, false);
		}
		
		protected void onPostExecute(String track) {
			 running = false;
			 Snipe.setVisibility(View.GONE);
			 if (showing) {
				 mNotificationManager.cancel(42);
				 showing = false;
			 }

			 int icon = R.drawable.ic_launcher;
			 CharSequence tickerText = "Finished!";
			 long when = System.currentTimeMillis();
			 CharSequence contentTitle = "Finished Sniping"; 
			 CharSequence contentText = "Click to view new songs."; 
			 
			 Intent notificationIntent = new Intent(getBaseContext(), ReportActivity.class);

			 PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, 0);

			 Notification notification = new Notification(icon, tickerText, when);
	   	     notification.defaults = Notification.DEFAULT_VIBRATE;
	   	     notification.flags = Notification.FLAG_AUTO_CANCEL;
			 notification.setLatestEventInfo(getBaseContext(), contentTitle, contentText, contentIntent);
			 final int HELLO_ID = 1;

			 mNotificationManager.notify(HELLO_ID, notification);
			 restart();
			 try {
				 c.close();	        
			 } catch (Exception e) {
				 
			 }
			 
		     Intent intent = new Intent(UIActivity.this, ReportActivity.class);
		     startActivity(intent);
		}
	     
	    @Override
		protected String doInBackground(String... ids) {	
		    	String fields[] = new String[16];
		        fields[0] = "_id";
		        fields[1] = "album_id";
		        fields[2] = "album";
		        fields[3] = "AlbumArtist";
		        fields[4] = "composer";
		        fields[5] = "_data";
		        fields[6] = "track";
		        fields[7] = "title";
		        fields[8] = "Genre";
		        fields[9] = "year";
		        fields[10] = "Size";
		        fields[11] = "DiscCount";
		        fields[12] = "DiscNumber";
		        fields[13] = "artistSort";
		        fields[14] = "CanonicalAlbum";
		        fields[15] = "CanonicalName";
	    		c = cR.query(Uri.parse(URI.uri), fields, null, null, null);
	    		try {
	    			c.moveToFirst();
	   			} catch (Exception e) {
	                mHandler.sendEmptyMessage(1);	    			
	   			}

	    		for (int i=0; i<ids.length; i++) {
	    			Log.d("SNIPE", Integer.toString(i));
	    			if (running) {
	    				String _id = ids[i].replace(".mp3", "");
	    				c = cR.query(Uri.parse(URI.uri), fields, "_id = "+_id, null, null);
	    				try {
		    				c.moveToFirst();
		    				shit(ids[i]);
		    				c.close();
	    				} catch (Exception e) {}
		   			} else {
		   				i = ids.length+1;
		   			}
		    	}
	        return ids[0];
		}

		private void shit(String id) throws ID3Exception {
	       		_id = c.getString(0);
			    album_id = c.getString(1);
			    album = c.getString(2);
		        albumArtist = c.getString(3);
		        composer = c.getString(4);
		        _data = c.getString(5);
		        track = c.getString(6);
		        title = c.getString(7);
		        genre = c.getString(8);
		        year = c.getString(9);
		        size = c.getString(10);
		        discCount = c.getString(11);
		        discNumber = c.getString(12);
		        artistSort = c.getString(13);
		        canonicalAlbum = c.getString(14);
		        canonicalName = c.getString(15);
		        	File song = new File(gMusic + id);
		        	File output = new File(Constants.gMusicSniperDir + "/music/" + albumArtist + "/" + album + "/" + title + ".mp3");
		        	File outputDir = new File(Constants.gMusicSniperDir + "/music/" + albumArtist + "/" + album + "/");
		        	if (!outputDir.isDirectory()) {
		        		outputDir.mkdirs();
		        	}
		        	
		            MediaFile oMediaFile = new MP3File(song);
		            ID3V2_3_0Tag tag = new ID3V2_3_0Tag();
		    		File art1 = new File(gDira+"/cache/artwork/"+album_id+".jpg");
		       		File art2 = new File(gDirb+album_id+".jpg");
		       		File art3 = new File(Constants.gMusicSniperDir + "/default_art.jpg");
		       		if (!art3.exists()) {
		       			copyAssets();
		       		}
		       		File file = null;
		       		if (art1.exists()) {
		       			file = art1;
		       		} else if (art2.exists()) {
		       			file = art2;
		       		} else {
		       			file = art3;
		       		}
		        	byte[] b = new byte[(int) file.length()];
		        	try {
		        	FileInputStream fileInputStream = new FileInputStream(file);
		        	fileInputStream.read(b);
		        	}
		        	catch (IOException e1) {
		        	e1.printStackTrace();
		        	}
		        	APICID3V2Frame newFrontCover = new APICID3V2Frame("image/jpeg",APICID3V2Frame.PictureType.FrontCover,"Album Cover",b);
		            tag.addAPICFrame(newFrontCover);		    		
		    		try {
						tag.setArtist(albumArtist);
					} catch (ID3Exception e) {
						e.printStackTrace();
					}
			        try {
						tag.setAlbum(album);
					} catch (ID3Exception e) {
						e.printStackTrace();
					}
			        try {
						tag.setTitle(title);
					} catch (ID3Exception e) {
						e.printStackTrace();
					}
			        try {
			        	tag.setGenre(genre);
					} catch (ID3Exception e) {
						e.printStackTrace();
					}
				    try {
						tag.setTrackNumber(Integer.parseInt(track));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (ID3Exception e) {
						e.printStackTrace();
					}
			        try {
						tag.setYear(Integer.parseInt(year));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (ID3Exception e) {
						e.printStackTrace();
					}

			        oMediaFile.setID3Tag(tag);
			        try {
						oMediaFile.sync();
					} catch (ID3Exception e) {
						e.printStackTrace();
					}	            
			      	try {
						copyFile(song, output);
					} catch (IOException e) {
						e.printStackTrace();
					}
			      	if (Constants.delete) {
			      		song.delete();
			      	}
			      	String[] id3 = {title, album, albumArtist, file.toString(), output.toString()};
		            bg_notification.contentView.setTextViewText(R.id.status_text, title);
			       	publishProgress(id3);
		}
    }
    
	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
		
	final Handler mHandler = new Handler(){ 
        public void handleMessage (Message  msg) {
        	switch (msg.what) {
        	case 0:
        		mProgressBar.setVisibility(View.INVISIBLE);
        		setupViews();
        	break;
        	
        	case 1:
        		AlertDialog.Builder builder = new AlertDialog.Builder(UIActivity.this);
		        builder.setTitle("Opps!");
			    builder.setMessage("Our request was denied.\nAre you sure your signed into Google Music?")
          		   .setCancelable(false)
          		   .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
          		       public void onClick(DialogInterface dialog, int id) {
			  				dialog.dismiss();
			        		mProgressBar.setVisibility(View.INVISIBLE);
          		       }
          		   });

          		AlertDialog alert = builder.create();
          		alert.show();
        	break;
        	
        	case 2:
        		restart();
        	}
        }
	}; 
	
	private void copyStream(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	public void copyFile(File sourceFile, File destFile)
            throws IOException {

		if (sourceFile.toString().endsWith("mp3")) {
			Log.d("SNIPE", sourceFile+":"+destFile);
			    InputStream in = new FileInputStream(sourceFile);
			    OutputStream out = new FileOutputStream(destFile);

			    byte[] buf = new byte[1024];
			    int len;
			    while ((len = in.read(buf)) > 0) {
			       out.write(buf, 0, len);
			    }
			    in.close();
			    out.close();
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
        Intent intent = new Intent(UIActivity.this, UIActivity.class);
        startActivity(intent);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.main_menu, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case R.id.settings:
            Intent si = new Intent(this, Settings.class);
            si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(si);
        break;
        case R.id.restart:
        	restart();
        break;
        case R.id.chooser:        
        Intent fi = new Intent(this, FileExplore.class);
        fi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(fi);
        break;
        case android.R.id.home:
            Intent hi = new Intent(this, UIActivity.class);
            hi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(hi);
            return true;
            default:
	            return super.onOptionsItemSelected(item);
	    }
		return false;
	}
	
	@Override 
	protected void onResume() {
		super.onResume();
		 mNotificationManager.cancel(42);
	     showing = false;
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		 mNotificationManager.cancel(42);
     	 showing = false;
	}
	
	@Override
	protected void onPause(){
		super.onPause();
        if (running && Constants.show) {
        	mNotificationManager.notify(42, bg_notification);
        	showing = true;
        }
    }
}
