package cn.edu.sysu.secretnote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ViewNotesActivity extends Activity {

	private Button refreshNotes;
	private Button publishNote;
	private ListView allNotes;
	private ArrayList<Map<String, String>> listItems;
	private SimpleAdapter adapter;
	private double latitude;
	private double longitute;
	static final int publishRequestCode = 0x0010;
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_notes);

		refreshNotes = (Button) findViewById(R.id.buttonRefresh);
		publishNote = (Button) findViewById(R.id.buttonPublish);
		allNotes = (ListView) findViewById(R.id.listViewNotes);
		listItems = new ArrayList<Map<String, String>>();
		adapter = new SimpleAdapter(this, listItems,
				android.R.layout.simple_list_item_2,
				new String[] { "消息", "发布人" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		allNotes.setAdapter(adapter);

		publishNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent getMessage = new Intent(ViewNotesActivity.this,
						WriteActivity.class);

				startActivityForResult(getMessage, publishRequestCode);
			}
		});

		refreshNotes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Map<String, String> data = new HashMap<String, String>();

				data.put("消息", "这是一条测试消息");
				data.put("发布人", "牛群");
				listItems.add(data);
				adapter.notifyDataSetChanged();
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

				StringBuilder builder = new StringBuilder();

				builder.append(data.getStringExtra("note"));
				builder.append("latitude = ");
				builder.append(latitude);
				builder.append(", longitude = ");
				builder.append(longitute);

				Toast toast = Toast.makeText(this, builder.toString(),
						Toast.LENGTH_SHORT);
				toast.show();
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
}
