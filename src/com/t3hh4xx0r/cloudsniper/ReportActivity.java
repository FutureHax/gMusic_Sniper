package com.t3hh4xx0r.cloudsniper;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ReportActivity extends Activity {
	ListView lv1;
    RelativeLayout progress;
	Button allB;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.results);

        lv1 = (ListView) findViewById(android.R.id.list);
        progress = (RelativeLayout) findViewById(R.id.progress);

        progress.setVisibility(View.GONE);

        final ArrayList<SniperResults> results = new ArrayList<SniperResults>();
        SniperResults resultsArray =  new SniperResults();    
        
    	for (int i=0;i<UIActivity.titles.size();i++) {
    		File f = new File(Constants.gMusicSniperDir+"/music/"+UIActivity.artists.get(i)+"/"+UIActivity.albums.get(i)+"/"+UIActivity.titles.get(i)+".mp3");
    		resultsArray = new SniperResults();
        	resultsArray.setAlbum(UIActivity.albums.get(i));
        	resultsArray.setTitle(UIActivity.titles.get(i));
        	resultsArray.setArtist(UIActivity.artists.get(i));
        	resultsArray.setArt(UIActivity.artwork.get(i));
        	resultsArray.setHasart(UIActivity.hasArt.get(i));
    		new MediaScannerNotifier(ReportActivity.this, f);     		
        	results.add(resultsArray);        	
        }
    	
        lv1.setAdapter(new ResultsAdapter(ReportActivity.this, results));
    }
    
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
