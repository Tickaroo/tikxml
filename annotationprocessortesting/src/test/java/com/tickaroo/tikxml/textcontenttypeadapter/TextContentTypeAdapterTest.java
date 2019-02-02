package com.tickaroo.tikxml.textcontenttypeadapter;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.textcontenttypeadapter.Ab;
import com.tickaroo.tikxml.annotationprocessing.textcontenttypeadapter.ABTypeAdapter;
import com.tickaroo.tikxml.annotationprocessing.textcontenttypeadapter.TextContentItem;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class TextContentTypeAdapterTest {

    @Test
    public void parseAndRead() throws IOException {
        TikXml xml = new TikXml.Builder()
                .exceptionOnUnreadXml(true)
                .addTypeAdapter(Ab.class, new ABTypeAdapter("ab"))
                .build();

        TextContentItem item = xml.read(TestUtils.sourceForFile("ab_type_adapter.xml"), TextContentItem.class);
        Assert.assertEquals(Ab.A, item.ab);
        Assert.assertEquals(Arrays.asList(Ab.A, Ab.B), item.items);

    }
}
