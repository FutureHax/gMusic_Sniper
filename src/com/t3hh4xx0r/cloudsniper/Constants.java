package com.t3hh4xx0r.cloudsniper;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

public final class Constants {
	
	public static File extStorageDirectory = Environment.getExternalStorageDirectory();
	
	public static String gCacheDir1 = extStorageDirectory + "/Android/data/com.google.android.music/cache/music/";
	
	public static String gCacheDir2 = extStorageDirectory + "/Android/data/com.android.music/cache/music/";
	
	public static String gMusicDir1 = extStorageDirectory + "/Android/data/com.google.android.music/";

	public static String gMusicDir2 = extStorageDirectory + "/Android/data/com.android.music/";

	public static String gMusicSniperDir = extStorageDirectory + "/t3hh4xx0r/gMusicSniper";
	
	public static String gMusicDB1 = "/data/data/com.google.android.music/databases/";

	public static String gMusicDB2 = "/data/data/com.android.music/databases/";
	
	public static boolean delete = false;

	public static boolean show = true;

	public static boolean showart = false;

}
