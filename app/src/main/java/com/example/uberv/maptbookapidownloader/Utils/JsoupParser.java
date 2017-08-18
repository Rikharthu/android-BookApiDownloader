package com.example.uberv.maptbookapidownloader.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class JsoupParser {

    public static final String IMAGE_FILENAME_REGEX = "(image.+..+)";

    public static List<String> parse(String html) {
        List<String> imageUrls = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        Elements images = doc.getElementsByTag("img");

        Pattern pattern = Pattern.compile(IMAGE_FILENAME_REGEX);

        Matcher m;
        Element image;
        for (int i = 0; i < images.size(); i++) {
            image = images.get(i);
            String imageUrl = image.attr("src");
            m = pattern.matcher(imageUrl);

            String imageFileName = null;
            if (m.find()) {
                imageFileName = m.group();
            } else {
                Timber.d("Could not find image name in url: " + imageUrl);
            }
            if (imageFileName != null) {
                // TODO do something
                imageUrls.add(imageUrl);
            }
        }
        return imageUrls;
    }
}
