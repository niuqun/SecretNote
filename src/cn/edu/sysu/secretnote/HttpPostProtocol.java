package cn.edu.sysu.secretnote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpPostProtocol {
	private ArrayList<NameValuePair> p;
	private ArrayList<NameValuePair> URL;

	public HttpPostProtocol() {
		p = new ArrayList<NameValuePair>();
		URL = new ArrayList<NameValuePair>();

		URL.add(new BasicNameValuePair("REGISTER",
				"http://10.0.2.2:8080/Calculator/servlet/CalculateServlet"));
		URL.add(new BasicNameValuePair("LOGIN",
				"http://10.0.2.2:8080/Calculator/servlet/CalculateServlet"));
		URL.add(new BasicNameValuePair("LOGOUT", "http://localhost:8008"));
		URL.add(new BasicNameValuePair("RECEIVE", "http://localhost:8008"));
		URL.add(new BasicNameValuePair("POST", "http://localhost:8080"));
	}

	public boolean sendHttpPostToServer(List<NameValuePair> paramsIn,
			List<NameValuePair> paramsOut) {
		p.clear();

		for (int i = 1; i < paramsIn.size(); ++i) {
			p.add(paramsIn.get(i));
		}

		String cmdURL = "";
		for (int i = 0; i < URL.size(); ++i) {
			if (paramsIn.get(0).getValue().equals(URL.get(i).getName())) {
				cmdURL = URL.get(i).getValue();
			}
		}

		HttpPost httpRequest = new HttpPost(cmdURL);
		try {
			HttpEntity entity = new UrlEncodedFormEntity(p);
			httpRequest.setEntity(entity);
			try {
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// read results from httpResponse

					String msg = "";
					String s = "";
					HttpEntity resEntity = httpResponse.getEntity();
					InputStream in = resEntity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));

					while ((s = reader.readLine()) != null) {
						msg += s;
					}
					paramsOut.add(new BasicNameValuePair("result", msg));

					return true;
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			return false;
		}

		return false;
	}
}
