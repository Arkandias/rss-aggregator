package com.rss.aggregator.desktop;

import java.net.URL;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class rssParser {
	
    static XmlReader reader = null;
	
    public static SyndFeed getRSSContent(String feedUrl) throws Exception {
    	SyndFeed feed = null;
    	try {
	        URL url = new URL(feedUrl);
			reader = new XmlReader(url);
			feed = new SyndFeedInput().build(reader);
	    } finally {
	      if (reader != null)
	        reader.close();
	    }
		return feed;
	  }
}