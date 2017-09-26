package com.tickaroo.tikxml.types;

import com.tickaroo.tikxml.TikXmlConfig;
import com.tickaroo.tikxml.XmlReader;
import com.tickaroo.tikxml.XmlWriter;
import com.tickaroo.tikxml.typeadapter.TypeAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringListTypeAdapter implements TypeAdapter<List<String>> {


    @Override
    public List<String> fromXml(XmlReader reader, TikXmlConfig config) throws IOException {

        List<String> list = new ArrayList<>();


        while (reader.hasElement()) {

            // <a>a<a>
            // <b>b</b>
            // <c>c</c>
            reader.beginElement();
            reader.nextElementName(); // Ignore element name
            String value = reader.nextTextContent();
            list.add(value);
            reader.endElement();
        }

        return list;
    }

    @Override
    public void toXml(XmlWriter writer, TikXmlConfig config, List<String> value, String overridingXmlElementTagName) throws IOException {

        writer.beginElement("items");

        for (String item : value) {
            writer.beginElement(item)
                    .textContent(item)
                    .endElement();
        }
        writer.endElement();

    }
}
