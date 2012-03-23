package com.t3hh4xx0r.cloudsniper;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectionAdapter extends BaseAdapter {
	ArrayList<SelectionResults> selectionList;
    static ArrayList<String> selections = new ArrayList<String>();
	
	 private LayoutInflater mInflater;
	 Context ctx;	 

	 public SelectionAdapter(Context context, ArrayList<SelectionResults> list) {
	  selectionList = list;
	  mInflater = LayoutInflater.from(context);
	  ctx = context;	  
	 }
	 
	 public int getCount() {
	  return selectionList.size();
	 }

	 public Object getItem(int position) {
	  return selectionList.get(position);
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(final int position, View convertView, ViewGroup parent) {
	  final ViewHolder holder;
	  if (convertView == null) {
		  convertView = mInflater.inflate(R.layout.selection_item, null);
		  holder = new ViewHolder();
		  holder.title = (TextView) convertView.findViewById(R.id.title);
		  holder.file = (TextView) convertView.findViewById(R.id.file);
		  holder.artist = (TextView) convertView.findViewById(R.id.artist);
		  holder.album = (TextView) convertView.findViewById(R.id.album);
		  holder.statusBox = (ImageView) convertView.findViewById(R.id.status_box);
		  convertView.setTag(holder);   	   	  
		  convertView.setOnClickListener(new OnClickListener() {
			  public void onClick(View v) {
				  if (selections.contains(holder.file.getText().toString())) {
					  for (int i=0;i<selections.size();i++) {
						  if (selections.get(i).equals(holder.file.getText().toString())) {
							  selections.remove(i);
							  holder.statusBox.setBackgroundResource(R.drawable.btn_check_off_focused_holo_dark);
						  }
					  }
				  } else {
					  selections.add(holder.file.getText().toString());
					  holder.statusBox.setBackgroundResource(R.drawable.btn_check_on_focused_holo_dark);
				  }
			  }
		  });
	  } else {
		  holder = (ViewHolder) convertView.getTag();
	  }
	  holder.title.setText(selectionList.get(position).getTitle());
	  holder.album.setText(selectionList.get(position).getAlbum());
	  holder.file.setText(selectionList.get(position).getFile());
	  holder.artist.setText(selectionList.get(position).getArtist());
	  if (selections.contains(holder.file.getText().toString())) {
		  holder.statusBox.setBackgroundResource(R.drawable.btn_check_on_focused_holo_dark);
	  } else {
		  holder.statusBox.setBackgroundResource(R.drawable.btn_check_off_focused_holo_dark); 
	  }
	  return convertView;
	 }

	 static class ViewHolder {
	  TextView artist;
	  TextView title;
	  TextView album;
	  TextView file;
	  ImageView statusBox;
	 }
	}