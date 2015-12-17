package com.tickaroo.tikxml.reading;

import com.tickaroo.tikxml.XmlReader;
import java.io.IOException;

/**
 * @author Hannes Dorfmann
 */
public interface XmlParser <T> {

  public T read(XmlReader reader) throws IOException;
}
