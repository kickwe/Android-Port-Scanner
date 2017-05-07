package com.kickwe.port.scanner;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;




public class netInfoAdapter extends ArrayAdapter<appNetInfo> {
    private final Context context;
    private final ArrayList<appNetInfo> values;
	private AdView mAdView;

    public netInfoAdapter(Context context, ArrayList<appNetInfo> values) {
        super(context, R.layout.activity_main, values);
        this.context = context;
        this.values = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_main, parent, false);
		
        TextView appInfo = (TextView) rowView.findViewById(R.id.appInfo);
        ImageView appIcon = (ImageView) rowView.findViewById(R.id.appIcon);
        String appName = values.get(position).getName();
		Button action = (Button) rowView.findViewById(R.id.action);
		MobileAds.initialize(context, "ca-app-pub-6330978291685189~8106161555");

		mAdView = (AdView)rowView.findViewById(R.id.adView);
		mAdView.bringToFront();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
		
        appInfo.setText(values.get(position).toString());
        if (values.get(position).getUID() == -1) {
            appIcon.setVisibility(View.GONE);
            return rowView;
        }
        Drawable icon = null;
        if (appName.contains(":")) {
            appName = appName.substring(0,appName.indexOf(":"));
        }
		final String appPackName=appName;
		action.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					// do stuff
					//Toast.makeText(context, appPackName, Toast.LENGTH_SHORT).show();
					
					final Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+appPackName));

					context.startActivity(i);
					
					
				}
			});
        try {
            icon = context.getPackageManager().getApplicationIcon(appName);
            appIcon.setBackground(icon);
        } catch (PackageManager.NameNotFoundException e){
            appIcon.setBackground(context.getPackageManager().getDefaultActivityIcon());
            e.printStackTrace();
        }
        return rowView;

    }

}
