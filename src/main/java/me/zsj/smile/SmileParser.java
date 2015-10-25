package me.zsj.smile;


import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.zsj.smile.model.Smile;
import me.zsj.smile.utils.CommonException;
import me.zsj.smile.utils.HttpUtils;
import me.zsj.smile.utils.OkHttpUtils;
import me.zsj.smile.utils.TaskUtils;


public class SmileParser {

    Smile smile = null;
    private static final String SMILE_URL = "http://www.yikedou.com";

    private static SmileParser INSTANCE = null;

    public static SmileParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SmileParser();
        }
        return INSTANCE;
    }


    public List<Smile> getSimle(String urlHtml) {

        List<Smile> mSmileLists = new ArrayList<>();
        String smileInfo = " ";

        /*try {
            smileInfo = HttpUtils.getInfo(urlHtml);
        } catch (CommonException e) {
            e.printStackTrace();
        }*/
        smileInfo = OkHttpUtils.run(urlHtml);
        Document document = Jsoup.parse(smileInfo);

        Elements inner = document.getElementsByClass("inner");
        Elements mElements = document.getElementsByTag("h3");
        for (int i = 0; i < mElements.size(); i++) {
            Element listElement = mElements.get(i);
            Element smileText = inner.get(i);

            smile = new Smile();

            Elements h3 = listElement.getElementsByTag("h3");
            String title = h3.text();
            Elements href = listElement.getElementsByTag("a");
            String url = href.attr("href");
            String smileContent = smileText.text();

            smile.setTitle(title);
            smile.setTitleUrl(SMILE_URL + url);
            smile.setSmileContent(smileContent);

            mSmileLists.add(smile);
        }

        return mSmileLists;

    }


    public String getJokeInfo(String url) {

        String htmlInfo = OkHttpUtils.run(url);
        Document document = Jsoup.parse(htmlInfo);
        Elements elements = document.getElementsByClass("arcBody");
        String jokeInfo = elements.text();
        return jokeInfo;
    }
}
