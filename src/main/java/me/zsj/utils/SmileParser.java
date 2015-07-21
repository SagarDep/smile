package me.zsj.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.zsj.model.Smile;


public class SmileParser {

	Smile smile = null;
	
	public List<Smile> getSimle(String urlHtml) throws CommonException {
		
		List<Smile> mSimleLists = new ArrayList<>();
		
		String smileInfo = HttpUtils.getInfo(urlHtml);
		Document document = Jsoup.parse(smileInfo);
		Elements elements = document.getElementsByClass("inner");
		for (int i = 0; i < elements.size(); i++) {
			
			smile = new Smile();

			Element innerElement = elements.get(i);
			String smileContent = innerElement.text();
			smile.setSmileContent(smileContent);

			mSimleLists.add(smile);
		}
		return mSimleLists;
		
	}
}
