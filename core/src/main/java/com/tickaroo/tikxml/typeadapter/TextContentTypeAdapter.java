package com.tickaroo.tikxml.typeadapter;

import com.tickaroo.tikxml.TikXmlConfig;
import com.tickaroo.tikxml.XmlReader;
import com.tickaroo.tikxml.XmlWriter;

import java.io.IOException;

public abstract class TextContentTypeAdapter<T> implements TypeAdapter<T> {
    private final String xmlTagName;

    public TextContentTypeAdapter(String xmlTagName) {
        this.xmlTagName = xmlTagName;
    }

    @Override
    public final T fromXml(XmlReader reader, TikXmlConfig config) throws IOException {
        // We ignore Attributes because this adapter only cares about TextContent
        /*
        while (reader.hasAttribute()) {
            reader.skipAttribute();
            reader.skipAttributeValue();
        }
        */

        T value = null;
        if (reader.hasTextContent()) {
            value = read(reader.nextTextContent());
        }
        reader.endElement();
        return value;
    }

    @Override
    public final void toXml(XmlWriter writer, TikXmlConfig config, T value, String overridingXmlElementTagName) throws IOException {
        if (overridingXmlElementTagName == null) {
            writer.beginElement(xmlTagName);
        } else {
            writer.beginElement(overridingXmlElementTagName);
        }

        writer.textContent(write(value));
        writer.endElement();

    }

    /**
     * Read a concrete value from it's string representation
     * @param value The value as String representation.
     * @return The parsed value
     */
    protected abstract T read(String value);

    /**
     * Writes a value to a String
     * @param value The value to write
     * @return A string representation of the value.
     */
    protected abstract String write(T value);
}
