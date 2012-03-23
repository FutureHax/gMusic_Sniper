package com.t3hh4xx0r.cloudsniper;

import java.io.File;

import android.content.Context;
import android.media.MediaScannerConnection; 
import android.media.MediaScannerConnection.MediaScannerConnectionClient; 
import android.net.Uri;

class MediaScannerNotifier implements MediaScannerConnectionClient { 
	private MediaScannerConnection mMs;
	private File mFile;

	public MediaScannerNotifier(Context context, File f) {
	    mFile = f;
	    mMs = new MediaScannerConnection(context, this);
	    mMs.connect();
	}

	@Override
	public void onMediaScannerConnected() {
	    mMs.scanFile(mFile.getAbsolutePath(), null);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
	    mMs.disconnect();
	}
}