package org.asciidoctor.extension;

import org.asciidoctor.ast.DocumentRuby;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;

public class ArrowsAndBoxesIncludesPostProcessor extends Postprocessor {

    public ArrowsAndBoxesIncludesPostProcessor(Map<String, Object> config) {
        super(config);
    }

    public ArrowsAndBoxesIncludesPostProcessor(DocumentRuby documentRuby) {
        //super(documentRuby);
        super(null);
    }

    //@Override
    public String process(DocumentRuby doc, String output) {
    
        Document document = Jsoup.parse(output);
        Element head = document.getElementsByTag("head").first();
        head.appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "http://www.headjump.de/stylesheets/arrowsandboxes.css");
        head.appendElement("script").attr("type", "text/javascript").attr("src", "http://code.jquery.com/jquery-1.4.1.min.js");
        head.appendElement("script").attr("type", "text/javascript").attr("src", "http://www.headjump.de/javascripts/jquery_wz_jsgraphics.js");
        head.appendElement("script").attr("type", "text/javascript").attr("src", "http://www.headjump.de/javascripts/arrowsandboxes.js");
        
        return document.html();
    }

}
