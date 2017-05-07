package com.kickwe.port.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.annotation.NonNull;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import java.security.Permission;
import android.Manifest;
public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
	private static final int STORAGE_PERMISSION_CODE = 23;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
		if (Build.VERSION.SDK_INT >= 23)
		{
			if(!checkAccess()){
				requestStoragePermission();
			} else {
				ActionBar actionBar = getSupportActionBar();
				actionBar.hide();

				new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
							SplashActivity.this.startActivity(mainIntent);
							SplashActivity.this.finish();
						}
					}, SPLASH_DISPLAY_LENGTH);
			}
		} else {
			ActionBar actionBar = getSupportActionBar();
			actionBar.hide();

			new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
						SplashActivity.this.startActivity(mainIntent);
						SplashActivity.this.finish();
					}
				}, SPLASH_DISPLAY_LENGTH);
		}
		
        
    }
	private Boolean checkAccess()
	{

		if(isReadStorageAllowed()){
			//If permission is already having then showing the toast
			
			//Existing the method with return
			Toast.makeText(this,"Permission granted for external srorage",Toast.LENGTH_LONG).show();
			return true;
		} else {
			return false;
		}
	}
	private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
		} else {
			//If permission is not granted returning false
			return false;
		}
    }
	//Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission

        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

				//     checkAccess();
				final Intent i = new Intent(SplashActivity.this, MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				finishAndRemoveTask();
				startActivity(i);
				//Displaying a toast
                Toast.makeText(this,"Permission granted for external srorage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
				//checkAccess(savedInstanceState);
				explain("You must grant storage pernission to use port scanner");
            }
        }
    }
	private void explain(String msg){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg)
			.setCancelable(false)
			//.setIcon(R.drawable.ic_launcher)
			//.setTitle("Permission Caution")
			.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					//  permissionsclass.requestPermission(type,code);
					final Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.kickwe.port.scanner"));
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					//i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					finishAndRemoveTask();
					startActivity(i);

				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					finish();
				}
			});
        dialog.show();
    }
}
