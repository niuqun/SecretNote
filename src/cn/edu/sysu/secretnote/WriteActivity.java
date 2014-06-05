package cn.edu.sysu.secretnote;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WriteActivity extends Activity {

	private EditText newNoteMessage;
	private Button publish;
	private Button cancel;
	private String note;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write);

		newNoteMessage = (EditText) findViewById(R.id.editTextWriteNote);
		publish = (Button) findViewById(R.id.buttonPublishNote);
		cancel = (Button) findViewById(R.id.buttonCancelPublish);

		publish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				if ((note = newNoteMessage.getText().toString()) != null) {
					intent.putExtra("note", note);
					setResult(RESULT_OK, intent);
				} else {
					setResult(0x2000, intent);
				}
				finish();
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			Intent intent = new Intent();

			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.write, menu);
		return true;
	}

}
