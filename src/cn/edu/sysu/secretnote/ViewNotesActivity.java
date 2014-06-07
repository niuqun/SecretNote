package cn.edu.sysu.secretnote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ViewNotesActivity extends Activity {

	private Button refreshNotes;
	private Button publishNote;
	private Button logout;
	private boolean bRefresh = false;
	private boolean bPublish = false;
	private ListView allNotes;
	private ArrayList<Map<String, String>> listItems;
	private SimpleAdapter adapter;
	private double latitude;
	private double longitute;
	static final int publishRequestCode = 0x0010;
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	private String userName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_notes);

		refreshNotes = (Button) findViewById(R.id.buttonRefresh);
		publishNote = (Button) findViewById(R.id.buttonPublish);
		logout = (Button) findViewById(R.id.buttonLogout);
		allNotes = (ListView) findViewById(R.id.listViewNotes);
		listItems = new ArrayList<Map<String, String>>();
		adapter = new SimpleAdapter(this, listItems,
				android.R.layout.simple_list_item_2,
				new String[] { "消息", "发布人" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		allNotes.setAdapter(adapter);

		Intent userNameIntent = getIntent();
		userName = userNameIntent.getStringExtra("userName");

		publishNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				bPublish = true;

				Intent getMessage = new Intent(ViewNotesActivity.this,
						WriteActivity.class);

				startActivityForResult(getMessage, publishRequestCode);
			}
		});

		refreshNotes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				bRefresh = true;
				initLocationServices();

				BasicNameValuePair b1 = new BasicNameValuePair("cmd", "RECEIVE");
				BasicNameValuePair b2 = new BasicNameValuePair("gpsLongitude",
						String.valueOf(longitute));
				BasicNameValuePair b3 = new BasicNameValuePair("gpsLatitude",
						String.valueOf(latitude));

				new NoteAsyncTask().execute(b1, b2, b3);
			}
		});

		logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BasicNameValuePair b1 = new BasicNameValuePair("cmd", "LOGOUT");
				BasicNameValuePair b2 = new BasicNameValuePair("userName",
						userName);

				new LogoutAsyncTask().execute(b1, b2);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_notes, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == publishRequestCode) {
			if (resultCode == RESULT_OK) {
				// get message
				initLocationServices();

				BasicNameValuePair b1 = new BasicNameValuePair("cmd", "POST");
				BasicNameValuePair b2 = new BasicNameValuePair("gpsLongitute",
						String.valueOf(longitute));
				BasicNameValuePair b3 = new BasicNameValuePair("gpsLatitude",
						String.valueOf(latitude));
				BasicNameValuePair b4 = new BasicNameValuePair("content",
						data.getStringExtra("note"));
				BasicNameValuePair b5 = new BasicNameValuePair("parentMessage",
						"");
				BasicNameValuePair b6 = new BasicNameValuePair("userName",
						userName);

				new NoteAsyncTask().execute(b1, b2, b3, b4, b5, b6);
			} else if (resultCode == 0x2000) {

			}
		}
	}

	private void initLocationServices() {
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {
				// make use of location
				latitude = arg0.getLatitude();
				longitute = arg0.getLongitude();
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
	}

	private class NoteAsyncTask extends AsyncTask<NameValuePair, Void, Void> {
		private ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		private ArrayList<NameValuePair> results = new ArrayList<NameValuePair>();

		@Override
		protected Void doInBackground(NameValuePair... params) {
			if (bRefresh) {
				HttpPostProtocol httpPost = new HttpPostProtocol();

				parameters.clear();
				results.clear();
				for (int i = 0; i < params.length; ++i) {
					parameters.add(params[i]);
				}
				httpPost.sendHttpPostToServer(parameters, results);
			}

			if (bPublish) {
				HttpPostProtocol httpPost = new HttpPostProtocol();

				parameters.clear();
				results.clear();
				for (int i = 0; i < params.length; ++i) {
					parameters.add(params[i]);
				}
				httpPost.sendHttpPostToServer(parameters, results);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (results.size() > 0) {
				if (bPublish) {
					Toast toast = Toast.makeText(ViewNotesActivity.this,
							"发送消息成功", Toast.LENGTH_SHORT);
					toast.show();

					bPublish = false;
				}

				if (bRefresh) {
					Map<String, String> data = new HashMap<String, String>();

					// data.put("消息", "李清照");
					// data.put("发布人", "春到长门春草青，江梅些子绿，未均匀");
					data.put("消息", results.get(2).getValue());
					data.put("发布人", results.get(1).getValue());
					listItems.add(data);
					adapter.notifyDataSetChanged();
					bRefresh = false;
				}
			} else {
				Toast toast = Toast.makeText(ViewNotesActivity.this,
						"发送接收消息失败", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	private class LogoutAsyncTask extends AsyncTask<NameValuePair, Void, Void> {
		private ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		private ArrayList<NameValuePair> results = new ArrayList<NameValuePair>();

		@Override
		protected Void doInBackground(NameValuePair... arg0) {
			HttpPostProtocol post = new HttpPostProtocol();

			parameters.clear();
			results.clear();
			for (int i = 0; i < arg0.length; ++i) {
				parameters.add(arg0[i]);
			}

			post.sendHttpPostToServer(parameters, results);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (results.get(0).getValue().equals("0x0")) {
				Intent intent = new Intent(Intent.ACTION_MAIN);

				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}

	}
}
