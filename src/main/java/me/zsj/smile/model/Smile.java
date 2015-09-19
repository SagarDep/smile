package me.zsj.smile.model;

import org.litepal.crud.DataSupport;

public class Smile extends DataSupport{

	private String smileContent;
	private String title;
	private String titleUrl;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleUrl() {
		return titleUrl;
	}

	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
	}

	public String getSmileContent() {
		return smileContent;
	}
	public void setSmileContent(String smileContent) {
		this.smileContent = smileContent;
	}

	@Override
	public String toString() {
		return "Smile{" +
				"smileContent='" + smileContent + '\'' +
				", title='" + title + '\'' +
				", titleUrl='" + titleUrl + '\'' +
				'}';
	}
}
