package com.android.laabroo.ngixings;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;

import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class MyItemizedOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
	Drawable marker;
	private Context mContext;

	public MyItemizedOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		// TODO Auto-generated constructor stub
		marker = defaultMarker;

	}

	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return (OverlayItem) items.get(arg0);
		// return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return items.size();
	}
	
	public void addItem(OverlayItem item) {
		// TODO Auto-generated method stub
		items.add(item);
		populate();
	}

	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = items.get(0);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.setPositiveButton("Close", new OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}

		});
		dialog.show();
		return true;
	}



}
