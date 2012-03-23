package com.t3hh4xx0r.cloudsniper;

import android.graphics.Bitmap;

public class SniperResults {
	 private String title = "unknown";
	 private String album = "unknown";
	 private String artist = "unknown";
	 private Bitmap art;

	 public void setTitle(String title) {
		  this.title = title;
		 }

		 public String getTitle() {
		  return title;
		 }

		 public void setAlbum(String album) {
			  this.album = album;
			 }

			 public String getAlbum() {
			  return album;
			 }

			 public void setArtist(String artist) {
				  this.artist = artist;
				 }

				 public String getArtist() {
				  return artist;
				 }

				 public void setArt(Bitmap art) {
					  this.art = art;
					 }

					 public Bitmap getArt() {
					  return art;
					 }				 
}
