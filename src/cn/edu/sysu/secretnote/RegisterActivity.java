package cn.edu.sysu.secretnote;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {

	private EditText userName;
	private EditText password;
	private EditText confirmPassword;
	private Button register;
	private Button reset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		userName = (EditText) findViewById(R.id.editTextNewUserName);
		password = (EditText) findViewById(R.id.editTextNewPassword);
		confirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
		register = (Button) findViewById(R.id.buttonRegisterNew);
		reset = (Button) findViewById(R.id.buttonReset);

		register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = userName.getText().toString();

				if (!isValidEmail(name)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegisterActivity.this);

					builder.setTitle(R.string.error);
					builder.setMessage(R.string.wrong_email_addr)
							.setCancelable(false).setPositiveButton("OK", null);
					builder.create();
					builder.show();
				} else {
					if (password.getText().toString() == null
							|| (!password
									.getText()
									.toString()
									.equals(confirmPassword.getText()
											.toString()))) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								RegisterActivity.this);

						builder.setTitle(R.string.error);
						builder.setMessage(R.string.unmatched_password)
								.setCancelable(false)
								.setPositiveButton("OK", null);
						builder.create();
						builder.show();
					} else {
						BasicNameValuePair b1 = new BasicNameValuePair("cmd",
								"REGISTER");
						BasicNameValuePair b2 = new BasicNameValuePair(
								"userName", userName.getText().toString());
						BasicNameValuePair b3 = new BasicNameValuePair(
								"userPassword", password.getText().toString());

						new RegisterAsyncTask().execute(b1, b2, b3);
					}
				}
			}
		});

		reset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				userName.setText("");
				password.setText("");
				confirmPassword.setText("");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	public boolean isValidEmail(String addr) {
		if (addr.length() > 0) {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(addr).matches();
		}
		return false;
	}

	class RegisterAsyncTask extends AsyncTask<NameValuePair, Void, Void> {

		private ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		private ArrayList<NameValuePair> results = new ArrayList<NameValuePair>();

		@Override
		protected Void doInBackground(NameValuePair... arg0) {
			HttpPostProtocol httpPost = new HttpPostProtocol();

			parameters.clear();
			for (int i = 0; i < arg0.length; ++i) {
				parameters.add(arg0[i]);
			}

			httpPost.sendHttpPostToServer(parameters, results);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (results.size() > 0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						RegisterActivity.this);

				if (results.get(0).getValue().equals("0x0")) {
					alert.setTitle(R.string.message);
					alert.setMessage(R.string.register_succeeded)
							.setCancelable(false).setPositiveButton("OK", null);
				} else {
					alert.setTitle(R.string.message);
					alert.setMessage(R.string.register_failed)
							.setCancelable(false).setPositiveButton("OK", null);
				}
				alert.create();
				alert.show();
			}
		}
	}
}
