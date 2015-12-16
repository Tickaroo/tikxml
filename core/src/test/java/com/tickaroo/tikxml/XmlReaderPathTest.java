package com.tickaroo.tikxml;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

import static com.tickaroo.tikxml.TestUtils.readerFrom;

/**
 * @author Hannes Dorfmann
 */
public class XmlReaderPathTest {

  @Test
  public void test() throws IOException {
    XmlReader reader = readerFrom("<element a='123' b=\"qwe\">value<bar a='123' /></element>");

    try {
      reader.beginElement();
      reader.nextElementName();
      Assert.assertEquals("/element", reader.getPath());

      reader.nextAttributeName();
      Assert.assertEquals("/element[@a]", reader.getPath());
      reader.nextAttributeValue();
      Assert.assertEquals("/element", reader.getPath());

      reader.nextAttributeName();
      Assert.assertEquals("/element[@b]", reader.getPath());

      reader.nextAttributeValue();
      Assert.assertEquals("/element", reader.getPath());

      reader.nextTextContent();
      Assert.assertEquals("/element/text()", reader.getPath());

      reader.beginElement();
      reader.nextElementName();
      Assert.assertEquals("/element/bar", reader.getPath());
      reader.nextAttributeName();
      Assert.assertEquals("/element/bar[@a]", reader.getPath());
      reader.nextAttributeValue();
      Assert.assertEquals("/element/bar", reader.getPath());
      reader.endElement();

      Assert.assertEquals("/element/text()", reader.getPath());
      reader.endElement();

      Assert.assertEquals("/", reader.getPath());
    } finally {
      reader.close();
    }
  }

}
