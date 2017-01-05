package com.rss.aggregator.desktop;

import java.net.HttpURLConnection;
import java.net.URL;

import com.sun.org.apache.commons.digester.rss.Channel;
import com.sun.org.apache.commons.digester.rss.Item;
import com.sun.org.apache.commons.digester.rss.RSSDigester;

public class rssParser {

    public static Item[] getContent(String feed) throws Exception {
        RSSDigester digester = new RSSDigester();

        URL url = new URL(feed);
        HttpURLConnection httpSource = (HttpURLConnection) url.openConnection();

        Channel channel = (Channel)digester.parse(httpSource.getInputStream());
        if (channel == null) {
            throw new Exception("can't communicate with " + url);
        }
//		System.out.println(channel.getPubDate());
		return channel.findItems();
    }
}