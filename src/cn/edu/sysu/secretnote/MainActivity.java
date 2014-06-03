package cn.edu.sysu.secretnote;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private EditText userName;
	private EditText password;
	private Button logon;
	private Button register;
	private boolean succeeded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		succeeded = false;
		userName = (EditText) findViewById(R.id.editTextUserName);
		password = (EditText) findViewById(R.id.editTextPassword);

		logon = (Button) findViewById(R.id.buttonLogon);
		register = (Button) findViewById(R.id.buttonRegister);

		logon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// authenticate user
				if (userName.getText().toString() == null
						|| password.getText().toString() == null) {

					AlertDialog.Builder alert = new AlertDialog.Builder(
							MainActivity.this);

					alert.setTitle(R.string.error);
					alert.setMessage(R.string.empty_username_password)
							.setCancelable(false).setPositiveButton("OK", null);
					alert.create();
					alert.show();
				} else {
					if (isEmailValid(userName.getText().toString())) {
						BasicNameValuePair b1 = new BasicNameValuePair("cmd",
								"LOGIN");
						BasicNameValuePair b2 = new BasicNameValuePair(
								"userName", userName.getText().toString());
						BasicNameValuePair b3 = new BasicNameValuePair(
								"userPassword", password.getText().toString());

						new AuthenUserAnsycTask().execute(b1, b2, b3);
					} else {
						AlertDialog.Builder alert = new AlertDialog.Builder(
								MainActivity.this);

						alert.setTitle(R.string.error);
						alert.setMessage(R.string.email_not_valid)
								.setCancelable(false)
								.setPositiveButton("OK", null);
						alert.create();
						alert.show();
					}
				}
			}
		});

		register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				intent.setClass(MainActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class AuthenUserAnsycTask extends AsyncTask<NameValuePair, Void, Void> {
		private ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		private ArrayList<NameValuePair> results = new ArrayList<NameValuePair>();

		@Override
		protected Void doInBackground(NameValuePair... params) {
			HttpPostProtocol post = new HttpPostProtocol();

			parameters.clear();
			for (int i = 0; i < params.length; ++i) {
				parameters.add(params[i]);
			}
			post.sendHttpPostToServer(parameters, results);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (results.size() > 0) {
				if (results.get(0).getValue().equals("0x0")) {
					succeeded = true;
				} else {
					succeeded = false;
				}
			} else {
				succeeded = false;
			}

			if (succeeded) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();

				intent.setClass(MainActivity.this, ViewNotesActivity.class);
				bundle.putString("userName", userName.getText().toString());
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						MainActivity.this);

				alert.setTitle(R.string.error);
				alert.setMessage(R.string.login_failed).setCancelable(false)
						.setPositiveButton("OK", null);
				alert.create();
				alert.show();
			}
		}
	}

	public boolean isEmailValid(String email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
}
