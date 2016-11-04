package com.me.chat.list;

public class ListData {
	private String content;
	public static final int SEND = 1;
	public static final int RECEIVER = 2;
	private int flag;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	private String time;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getFlag() {
		return flag;
	}

	public ListData(String content, int flag, String time) {
		super();
		this.content = content;
		this.flag = flag;
		this.time = time;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

}
