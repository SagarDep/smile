package me.zsj.model;

import org.litepal.crud.DataSupport;

public class Smile extends DataSupport{

	private String smileContent;


	public String getSmileContent() {
		return smileContent;
	}
	public void setSmileContent(String smileContent) {
		this.smileContent = smileContent;
	}
	
	
}
