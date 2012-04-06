package com.t3hh4xx0r.cloudsniper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

public class Settings extends PreferenceActivity {
	Preference mStorage;
	CheckBoxPreference mDelete;
	CheckBoxPreference mShow;
	CheckBoxPreference mShowArt;
    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings_menu);
		
		mDelete = (CheckBoxPreference) findPreference("delete");
        mDelete.setChecked(Constants.delete);
		mShow = (CheckBoxPreference) findPreference("show");
        mShow.setChecked(Constants.show);
		mShowArt = (CheckBoxPreference) findPreference("showart");
        mShowArt.setChecked(Constants.showart);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	 boolean value;
	    if(preference == mDelete){
            value = mDelete.isChecked();
            if(value) {
                Constants.delete = true;
            } else {
                Constants.delete = false;
            }
        }
	    if(preference == mShow){
            value = mShow.isChecked();
            if(value) {
                Constants.show = true;
            } else {
                Constants.show = false;
            }
        }
	    if(preference == mShowArt){
            value = mShowArt.isChecked();
            if(value) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Warning");
        	    builder.setMessage("This is a memory intensive option. This may cause FCs on some devices.\nAre you sure you want to continue?")
          		   .setCancelable(false)
          		   .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
          		       public void onClick(DialogInterface dialog, int id) {
        	  				dialog.dismiss();
        	                Constants.showart = true;
        	                mShowArt.setChecked(true);
          		       }
          		   })          		   
          		   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          		       public void onClick(DialogInterface dialog, int id) {
        	  				dialog.dismiss();
        	                Constants.showart = false;
        	                mShowArt.setChecked(false);
          		       }
          		   });
          		AlertDialog alert = builder.create();
          		alert.show();
            } else {
                Constants.showart = false;
            }
        }
	return true;
   }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, UIActivity.class);
            startActivity(intent);
            return true;
            default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
