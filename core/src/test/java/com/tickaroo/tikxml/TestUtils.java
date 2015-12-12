package com.tickaroo.tikxml;

import okio.Buffer;

/**
 * @author Hannes Dorfmann
 */
public class TestUtils {

  private TestUtils() {

  }


  public static XmlReader readerFrom(String xml) {
    return XmlReader.of(new Buffer().writeUtf8(xml));
  }

}
