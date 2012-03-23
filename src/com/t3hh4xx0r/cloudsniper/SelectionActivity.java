package com.t3hh4xx0r.cloudsniper;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectionActivity extends Activity {
	Button allB;
	
	ListView lv1;
	
    ContentResolver cR;

    Cursor c;
        
    TextView fName;
    
    RelativeLayout progress;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.results);
        cR = getContentResolver();

        SelectionAdapter.selections.clear();
        
        lv1 = (ListView) findViewById(android.R.id.list);
        progress = (RelativeLayout) findViewById(R.id.progress);
        fName = (TextView) findViewById(R.id.progress_name);
        allB = (Button) findViewById(R.id.all_button);
        allB.setOnClickListener(new OnClickListener() {
			  public void onClick(View v) {
			    	String[] songIds = SelectionAdapter.selections.toArray(new String[SelectionAdapter.selections.size()]);
		            Intent intent = new Intent(v.getContext(), UIActivity.class);
		            Bundle b = new Bundle();
		            b.putStringArray("tracks", songIds);
		            intent.putExtras(b);
		            startActivity(intent);
			  }
		  });
        new CreateArrayListTask().execute();
    }
    
    private class CreateArrayListTask extends AsyncTask<String, String, ArrayList<SelectionResults>> {       
        final ArrayList<SelectionResults> results = new ArrayList<SelectionResults>();
		
		protected ArrayList<SelectionResults> doInBackground(String... params) {        
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
    		c = cR.query(Uri.parse("content://com.google.android.music.MusicContent/audio?order=artist"), fields, null, null, null);
    		try {
    			c.moveToFirst();
   			} catch (Exception e) {
                mHandler.sendEmptyMessage(1);	    			
   			}
//    		for (int i=0; i<20; i++) {
    		for (int i=0; i<UIActivity.songIds.length; i++) {
	        	String _id = UIActivity.songIds[i].replace(".mp3", "");
	    		c = cR.query(Uri.parse("content://com.google.android.music.MusicContent/audio?order=artist"), fields, "_id = "+_id, null, null);
	    		try {
	    			c.moveToFirst();
	   				shit(UIActivity.songIds[i]);
	   				c.close();
	   			} catch (Exception e) {
	   			}
    		}
 			return results;
		}
		
		protected void onProgressUpdate(String... id) {
			fName.setText("Scanning..."+id[0]);
		}
		
        protected void onPostExecute(ArrayList<SelectionResults> results) {
        	progress.setVisibility(View.GONE);
        	allB.setVisibility(View.VISIBLE);
	        lv1.setAdapter(new SelectionAdapter(SelectionActivity.this, results));
        }
        
		private void shit(String id) {
	        SelectionResults resultsArray =  new SelectionResults();
		    String album = c.getString(2);
	        String albumArtist = c.getString(3);
	        String title = c.getString(7);

	    	resultsArray = new SelectionResults();
	    	resultsArray.setFile(id);
	    	resultsArray.setAlbum(album);
	    	resultsArray.setTitle(title);
	    	resultsArray.setArtist(albumArtist);
	    	results.add(resultsArray);
	    	publishProgress(id);
		}
    }
    
	final Handler mHandler = new Handler(){ 
        public void handleMessage (Message  msg) {
        	switch (msg.what) {
        	case 0:

        	break;
        	
        	case 1:
        		AlertDialog.Builder builder = new AlertDialog.Builder(SelectionActivity.this);
		        builder.setTitle("Opps!");
			    builder.setMessage("Our request was denied.\nAre you sure your signed into Google Music?")
          		   .setCancelable(false)
          		   .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
          		       public void onClick(DialogInterface dialog, int id) {
			  				dialog.dismiss();
          		       }
          		   });

          		AlertDialog alert = builder.create();
          		alert.show();
        	break;
        	
        	case 2:
        	}
        }
	}; 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, UIActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
            default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
