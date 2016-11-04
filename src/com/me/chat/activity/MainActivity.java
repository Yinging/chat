package com.me.chat.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.me.chat.R;
import com.me.chat.adapter.TextAdapter;
import com.me.chat.list.DictationResult;
import com.me.chat.list.ListData;
import com.me.chat.net.HttpData;
import com.me.chat.net.HttpGetDataListener;

public class MainActivity extends Activity implements HttpGetDataListener,
		OnClickListener {
	private static String APPID = "581c40d7";
	// 听写结果字符串（多个Json的列表字符串）
	private String dictationResultStr = "[";
	private String listen_droph;

	private Button listenBtn;// 语音Button
	private HttpData httpData;
	private List<ListData> lists;
	private ListView lv;
	private EditText sendText;
	private Button send_btn;
	private String content_str;
	private TextAdapter adapter;
	private String[] welcome_array;
	private double currentTime, oldTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();

	}

	private void initView() {
		lv = (ListView) findViewById(R.id.lv);
		sendText = (EditText) findViewById(R.id.sendText);
		send_btn = (Button) findViewById(R.id.send_btn);
		lists = new ArrayList<ListData>();
		listenBtn = (Button) findViewById(R.id.listen_btn);

		send_btn.setOnClickListener(this);
		listenBtn.setOnClickListener(this);
		adapter = new TextAdapter(lists, this);

		lv.setAdapter(adapter);
		ListData listData;
		listData = new ListData(getRandomWelcomTips(), ListData.RECEIVER,
				getTime());
		lists.add(listData);

	}

	@Override
	public void getDataUrl(String data) {
		System.out.println(data);
		parseText(data);
	}

	// 解析文字
	public void parseText(String str) {
		try {
			JSONObject jb = new JSONObject(str);
			ListData listData;
			listData = new ListData(jb.getString("text"), ListData.RECEIVER,
					getTime());
			lists.add(listData);
			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 发送输入的文本字符串
		case R.id.send_btn:
			getTime();
			content_str = sendText.getText().toString();
			sendText.setText("");
			// 去掉空格
			String dropk = content_str.replace(" ", "");
			String droph = dropk.replace("\n", "");
			ListData listData;
			listData = new ListData(content_str, ListData.SEND, getTime());
			lists.add(listData);
			adapter.notifyDataSetChanged();
			httpData = (HttpData) new HttpData(
					"http://www.tuling123.com/openapi/api?key=f2e068e301b529a1c96db048ad1e5db9&info="
							+ droph, MainActivity.this).execute();
			break;
		// 发送是识别出的语音
		case R.id.listen_btn:

			dictationResultStr = "[";
			// 语音配置对象初始化
			SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID
					+ "=" + APPID);

			// 1.创建SpeechRecognizer对象，第2个参数：本地听写时传InitListener
			SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(
					MainActivity.this, null);
			// 交互动画
			RecognizerDialog iatDialog = new RecognizerDialog(
					MainActivity.this, null);
			// 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
			mIat.setParameter(SpeechConstant.DOMAIN, "iat"); // domain:域名
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			mIat.setParameter(SpeechConstant.ACCENT, "mandarin"); // mandarin:普通话

			// 3.开始听写
			iatDialog.setListener(new RecognizerDialogListener() {

				@Override
				public void onResult(RecognizerResult results, boolean isLast) {
					// TODO 自动生成的方法存根
					// Log.d("Result", results.getResultString());
					// contentTv.setText(results.getResultString());
					if (!isLast) {
						dictationResultStr += results.getResultString() + ",";
					} else {
						dictationResultStr += results.getResultString() + "]";
					}
					if (isLast) {
						// 解析Json列表字符串
						Gson gson = new Gson();
						List<DictationResult> dictationResultList = gson
								.fromJson(dictationResultStr,
										new TypeToken<List<DictationResult>>() {
										}.getType());
						String finalResult = "";
						for (int i = 0; i < dictationResultList.size() - 1; i++) {
							finalResult += dictationResultList.get(i)
									.toString();
						}

						String listen_droph = finalResult;
						getTime();
						ListData listData1;
						listData1 = new ListData(listen_droph, ListData.SEND,
								getTime());
						lists.add(listData1);
						adapter.notifyDataSetChanged();
						httpData = (HttpData) new HttpData(
								"http://www.tuling123.com/openapi/api?key=f2e068e301b529a1c96db048ad1e5db9&info="
										+ listen_droph, MainActivity.this)
								.execute();
						Log.d("From reall phone", finalResult);
					}
				}

				@Override
				public void onError(SpeechError error) {
					// TODO 自动生成的方法存根
					error.getPlainDescription(true);
				}
			});
			// 开始听写
			iatDialog.show();

			break;

		}

	}

	// 获取随机的欢迎语
	private String getRandomWelcomTips() {
		String welcome_tip = null;
		welcome_array = this.getResources()
				.getStringArray(R.array.welcome_tips);
		int index = (int) (Math.random() * (welcome_array.length - 1));
		welcome_tip = welcome_array[index];
		return welcome_tip;

	}

	// 获取时间
	private String getTime() {
		currentTime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date curDate = new Date();
		String str = format.format(curDate);
		if (currentTime - oldTime >= 3 * 60 * 1000) {
			oldTime = currentTime;
			return str;
		}
		return "";
	}

}
