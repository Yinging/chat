package com.me.chat.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.os.AsyncTask;

public class HttpData extends AsyncTask<String, Void, String> {

	private HttpClient mHttpClient; // http请求
	private HttpGet mHttpGet; // get请求方式
	private HttpResponse mHttpResponse;
	private HttpEntity mHttpEntity;
	private String url;
	private InputStream in;
	private HttpGetDataListener listener;

	public HttpData(String url, HttpGetDataListener listener) {
		this.url = url;
		this.listener = listener;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			mHttpClient = new DefaultHttpClient();
			mHttpGet = new HttpGet(url);
			mHttpResponse = mHttpClient.execute(mHttpGet);
			mHttpEntity = mHttpResponse.getEntity();
			in = mHttpEntity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		listener.getDataUrl(result);
		super.onPostExecute(result);
	}
}
