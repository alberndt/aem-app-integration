package com.alexanderberndt.appintegration.engine.resourcetypes.html;

import com.alexanderberndt.appintegration.engine.resources.conversion.AbstractTextParser;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;

@Component(service = TextParser.class)
public class HtmlParser extends AbstractTextParser<Document> {

    public HtmlParser() {
        super(Document.class);
    }

    @Override
    public Object parse(@Nonnull Reader reader) throws IOException {
        return Jsoup.parse(IOUtils.toString(reader));
    }

    @Override
    protected String serializeType(@Nonnull Document source) {
        return source.outerHtml();
    }

}
