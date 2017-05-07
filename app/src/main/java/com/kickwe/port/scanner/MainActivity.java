package com.kickwe.port.scanner;
import android.app.ListActivity;
import java.util.Map;
import java.util.HashMap;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.os.Environment;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import android.view.Menu;
import android.content.Context;
import android.view.MenuItem;
import org.json.JSONArray;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import org.json.JSONObject;
import android.content.Intent;
import com.kickwe.port.scanner.JSONParser;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import org.json.JSONException;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;



public class MainActivity extends ListActivity {
	//URL to get JSON Array
    private static String url = "https://kickwe.com/kick.json";

    //JSON Node Names
    private static final String TAG_DATA = "data";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_URL = "url";
	private static final String TAG_STATUS = "status";

    JSONArray data = null;
    private Map<Integer, appNetInfo> apps = new HashMap<Integer, appNetInfo>();
    private final Map<String, String> STATE = stateMap();
    private PackageManager pm;
    private final ArrayList<appNetInfo> appInfos = new ArrayList<>();
    private netInfoAdapter adapter;

    private Map<String, String> stateMap() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("01", "ESTABLISHED");
        m.put("02", "SYN_SENT");
        m.put("03", "SYN_RECV");
        m.put("04", "FIN_WAIT1");
        m.put("05", "FIN_WAIT2");
        m.put("06", "TIME_WAIT");
        m.put("07", "CLOSE");
        m.put("08", "CLOSE_WAIT");
        m.put("09", "LAST_ACK");
        m.put("0A", "LISTEN");
        m.put("0B", "CLOSING");
        return Collections.unmodifiableMap(m);
    }

    private void buildNetstat () {
        apps.clear();
        appInfos.clear();
        parseProcNetTcp();
        parseProcNetTcp6();
        parseProcNetUdp();
        parseProcNetUdp6();
        for (appNetInfo obj : apps.values()) {
            appInfos.add(obj);
        }
    }

    private void parseProcNetTcp () {
        Process ns = null;
        try {
            ns = Runtime.getRuntime().exec("cat /proc/net/tcp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(ns.getInputStream()));
        int read;
        char[] buffer = new char[4096];
        StringBuffer output = new StringBuffer();
        try {
            while ((read = br.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ns.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] lines = output.toString().trim().split("\\r?\\n");
        //String TAG = "TCP";

        for (int i = 1; i < lines.length; i++) {
            //Log.v(TAG, lines[i]);
            String[] data = lines[i].trim().split("\\s+");
            int uid = Integer.parseInt(data[7]);
			if(uid!=0 ){
				if (!apps.containsKey(uid)) {
					apps.put(uid, new appNetInfo(uid, pm.getNameForUid(uid)));
				}
				apps.get(uid).addTcp("" + Integer.parseInt(data[4].substring(0, 8), 16) + " "
									 + Integer.parseInt(data[4].substring(9, 16), 16) + " "
									 + Integer.parseInt(data[1].substring(6, 8), 16) + "."
									 + Integer.parseInt(data[1].substring(4, 6), 16) + "."
									 + Integer.parseInt(data[1].substring(2, 4), 16) + "."
									 + Integer.parseInt(data[1].substring(0, 2), 16) + ":"
									 + Integer.parseInt(data[1].substring(9), 16) + " "
									 + Integer.parseInt(data[2].substring(6, 8), 16) + "."
									 + Integer.parseInt(data[2].substring(4, 6), 16) + "."
									 + Integer.parseInt(data[2].substring(2, 4), 16) + "."
									 + Integer.parseInt(data[2].substring(0, 2), 16) + ":"
									 + Integer.parseInt(data[2].substring(9), 16) + " "
									 + STATE.get(data[3]) + " ");
			}
			
            
         }
    }

    private void parseProcNetTcp6 () {
        Process ns = null;
        try {
            ns = Runtime.getRuntime().exec("cat /proc/net/tcp6");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(ns.getInputStream()));
        int read;
        char[] buffer = new char[4096];
        StringBuffer output = new StringBuffer();
        try {
            while ((read = br.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ns.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] lines = output.toString().trim().split("\\r?\\n");

        //String TAG = "TCP6";

        for (int i = 1; i < lines.length; i++) {
            //Log.v(TAG, lines[i]);
            String[] data = lines[i].trim().split("\\s+");
            int uid = Integer.parseInt(data[7]);
			
			if(uid!=0){

			
            if (!apps.containsKey(uid)) {
                apps.put(uid, new appNetInfo(uid, pm.getNameForUid(uid)));
            }
            apps.get(uid).addTcp6("" + Integer.parseInt(data[4].substring(0, 8), 16) + " "
                    + Integer.parseInt(data[4].substring(9, 16), 16) + " "
                    + decodeIp6(data[1].substring(0, 32)) + ":"
                    + Integer.parseInt(data[1].substring(33), 16) + " "
                    + decodeIp6(data[2].substring(0, 32)) + ":"
                    + Integer.parseInt(data[2].substring(33), 16) + " "
                    + STATE.get(data[3]) + " ");
        }
		}
    }

    private String decodeIp6 (String s) {
        StringBuilder ip6 = new StringBuilder();
        for (int i = 0; i < s.length(); i += 4) {
            if (s.substring(i, i+4).equalsIgnoreCase("0000")) {
              //  ip6.append(":");
                continue;
            }
            if (i == 16 && s.substring(i, i+4).equalsIgnoreCase("FFFF")) {
               // ip6.append("FFFF:");
                ip6.append("" + Integer.parseInt(s.substring(30, 32), 16) + "."
                        + Integer.parseInt(s.substring(28, 30), 16) + "."
                        + Integer.parseInt(s.substring(26, 28), 16) + "."
                        + Integer.parseInt(s.substring(24, 26), 16));
                return ip6.toString();
            }
          //  ip6.append(s.substring(i, i + 4) + ((i == 28) ? "" : ":"));
        }
        return ip6.toString();
    }

    private void parseProcNetUdp () {
        Process ns = null;
        try {
            ns = Runtime.getRuntime().exec("cat /proc/net/udp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(ns.getInputStream()));
        int read;
        char[] buffer = new char[4096];
        StringBuffer output = new StringBuffer();
        try {
            while ((read = br.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ns.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] lines = output.toString().trim().split("\\r?\\n");

        //String TAG = "UDP";

        for (int i = 1; i < lines.length; i++) {
            //Log.v(TAG, lines[i]);
            String[] data = lines[i].trim().split("\\s+");
            int uid = Integer.parseInt(data[7]);
			if(uid!=0){

			
			
            if (!apps.containsKey(uid)) {
                apps.put(uid, new appNetInfo(uid, pm.getNameForUid(uid)));
            }
            apps.get(uid).addUdp("" + Integer.parseInt(data[4].substring(0, 8), 16) + " "
                    + Integer.parseInt(data[4].substring(9, 16), 16) + " "
                    + Integer.parseInt(data[1].substring(6, 8), 16) + "."
                    + Integer.parseInt(data[1].substring(4, 6), 16) + "."
                    + Integer.parseInt(data[1].substring(2, 4), 16) + "."
                    + Integer.parseInt(data[1].substring(0, 2), 16) + ":"
                    + Integer.parseInt(data[1].substring(9), 16) + " "
                    + Integer.parseInt(data[2].substring(6, 8), 16) + "."
                    + Integer.parseInt(data[2].substring(4, 6), 16) + "."
                    + Integer.parseInt(data[2].substring(2, 4), 16) + "."
                    + Integer.parseInt(data[2].substring(0, 2), 16) + ":"
                    + Integer.parseInt(data[2].substring(9), 16) + " "
                    + STATE.get(data[3]) + " ");
        }
		}
    }

    private void parseProcNetUdp6 () {
        Process ns = null;
        try {
            ns = Runtime.getRuntime().exec("cat /proc/net/udp6");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(ns.getInputStream()));
        int read;
        char[] buffer = new char[4096];
        StringBuffer output = new StringBuffer();
        try {
            while ((read = br.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ns.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] lines = output.toString().trim().split("\\r?\\n");

        //String TAG = "UDP6";

        for (int i = 1; i < lines.length; i++) {
            //Log.v(TAG, lines[i]);
            String[] data = lines[i].trim().split("\\s+");
            int uid = Integer.parseInt(data[7]);
			
			if(uid!=0){

			
			
            if (!apps.containsKey(uid)) {
                apps.put(uid, new appNetInfo(uid, pm.getNameForUid(uid)));
            }
            apps.get(uid).addUdp6("" + Integer.parseInt(data[4].substring(0, 8), 16) + " "
                    + Integer.parseInt(data[4].substring(9, 16), 16) + " "
                    + decodeIp6(data[1].substring(0, 32)) + ":"
                    + Integer.parseInt(data[1].substring(33), 16) + " "
                    + decodeIp6(data[2].substring(0, 32)) + ":"
                    + Integer.parseInt(data[2].substring(33), 16) + " "
                    + STATE.get(data[3]) + " ");
        }
		}
    }

    private boolean saveFile() {
        File file;
        File dir;
        FileOutputStream outputStream;
        StringBuilder output = new StringBuilder();
        for (appNetInfo obj : appInfos) {
            output.append(obj.toString());
        }
        try {
            dir = new File(Environment.getExternalStorageDirectory(), "Port Scanner");
            if (!dir.exists()) {
                dir.mkdir();
            }
            file = new File(Environment.getExternalStorageDirectory() +"/Port Scanner", "KWPS-" +appInfos.get(0).toString().substring(0,19) +".txt");
            outputStream = new FileOutputStream(file);
            outputStream.write(output.toString().getBytes());
            outputStream.close();
            Toast.makeText(getApplicationContext(), "File saved as " +file.toString(), Toast.LENGTH_LONG).show();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "File save error", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        pm = getPackageManager();
        adapter = new netInfoAdapter(this, appInfos);
        setListAdapter(adapter);
        adapter.clear();
        buildNetstat();
        adapter.notifyDataSetChanged();
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", appInfos.get(position).toString());
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });
		if(haveNetworkConnection()){
			new JSONParse().execute();
		}
    }
	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
	private class JSONParse extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

		@Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }
		@Override
		protected void onPostExecute(JSONObject json) {
			pDialog.dismiss();
			try {
				// Getting JSON Array
				data = json.getJSONArray(TAG_DATA);
				JSONObject c = data.getJSONObject(0);

				// Storing  JSON item in a Variable
				String status = c.getString(TAG_STATUS);
				String message = c.getString(TAG_MESSAGE);
				String targetUrl = c.getString(TAG_URL);
				if(status.equals("1")){
					push(message,targetUrl);
				}
				//Set JSON Data in TextView
//				uid.setText(id);
//				name1.setText(name);
//				email1.setText(email);

            } catch (JSONException e) {
                e.printStackTrace();
            }

		}
    }
	private void push(final String msg, final String url){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg)
			.setCancelable(true)
			//.setIcon(R.drawable.ic_launcher)
			//.setTitle("Permission Caution")
			.setPositiveButton("Visit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					//  permissionsclass.requestPermission(type,code);
					final Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);

				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					paramDialogInterface.cancel();
				}
			});
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
	
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            adapter.clear();
            buildNetstat();
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Port Scanner Refreshed", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_save) {
            saveFile();
            return true;
        } else if (id == R.id.action_quit) {
            System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }
}
