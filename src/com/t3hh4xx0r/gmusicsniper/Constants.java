package com.t3hh4xx0r.gmusicsniper;

import java.io.File;

import android.os.Environment;

public final class Constants {
	
	public static File extStorageDirectory = Environment.getExternalStorageDirectory();
	
	public static String gCacheDir = extStorageDirectory + "/Android/data/com.google.android.music/cache/Music/";
	
	public static String gMusicSniperDir = extStorageDirectory + "/t3hh4xx0r/gMusicSniper/";

}
