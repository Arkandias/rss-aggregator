package com.rss.aggregator.desktop;
/*
import org.apache.commons.digester.rss.Channel;
import org.apache.commons.digester.rss.Item;
import org.apache.commons.digester.rss.RSSDigester;
*/
//import org.apache.commons.digester.Digester;
import com.sun.org.apache.commons.digester.*;
import org.apache.commons.collections.ArrayStack;
import com.sun.org.apache.commons.digester.rss.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class rssParser {

    public static void getContent() throws Exception {
        RSSDigester digester = new RSSDigester();
//        digester.setUseContextClassLoader(true);
        String feed = "http://www.rgagnon.com/feed.xml";
        URL url = new URL(feed);
        HttpURLConnection httpSource = (HttpURLConnection) url.openConnection();

        Channel channel = (Channel)digester.parse(httpSource.getInputStream());
        if (channel==null) {
            throw new Exception("can't communicate with " + url);
        }

        Item rssItems[]=channel.findItems();
        for (int i=0;i<rssItems.length;i++) {
           System.out.println(rssItems[i].getTitle());
           System.out.println(rssItems[i].getLink());
           System.out.println(rssItems[i].getDescription());
           System.out.println();
        }

        /*
              to parse from a file instead of a URL

              import java.io.FileInputStream;
              ...
              String feed = "feed.xml"
              FileInputStream fis = new FileInputStream(feed);
              Channel channel=(Channel)digester.parse(fis);
              ...
       */
    }
}