package com.tickaroo.tikxml;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.tickaroo.tikxml.TestUtils.readerFrom;

/**
 * @author Hannes Dorfmann
 */
public class XmlReaderTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void readObjectWithAttributes() throws IOException {

    String xml = "<element a=\"qwe\" b='123' c=\"skipMe\"></element>";
    XmlReader reader = readerFrom(xml);

    try {


      Assert.assertEquals(reader.peek(), XmlReader.XmlToken.ELEMENT_BEGIN);
      reader.beginElement();

      Assert.assertEquals(XmlReader.XmlToken.ELEMENT_NAME, reader.peek());
      Assert.assertEquals("element", reader.nextElementName());
      Assert.assertTrue(reader.hasAttribute());

      Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_NAME, reader.peek());
      Assert.assertEquals("a", reader.nextAttributeName());

      Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_VALUE, reader.peek());
      Assert.assertEquals("qwe", reader.nextAttributeValue());


      Assert.assertTrue(reader.hasAttribute());
      Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_NAME, reader.peek());
      Assert.assertEquals("b", reader.nextAttributeName());

      Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_VALUE, reader.peek());
      Assert.assertEquals("123", reader.nextAttributeValue());


      Assert.assertTrue(reader.hasAttribute());
      Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_NAME, reader.peek());
      Assert.assertEquals("c", reader.nextAttributeName());

      reader.skipAttributeValue();

      Assert.assertFalse(reader.hasAttribute());

    } finally {
      reader.close();
    }
  }

  @Test
  public void attributeWithDoubleEqualsSign() throws IOException {
    String xml = "<element a==\"qwe\"></element>";
    XmlReader reader = readerFrom(xml);
    reader.beginElement();
    reader.nextElementName();
    reader.nextAttributeName();
    exception.expect(IOException.class);
    reader.nextAttributeValue();
    reader.close();
  }


  @Test
  public void attributeNoQuotes() throws IOException {
    String xml = "<element a=qwe></element>";
    XmlReader reader = readerFrom(xml);
    reader.beginElement();
    reader.nextElementName();
    reader.nextAttributeName();
    exception.expect(IOException.class);
    reader.nextAttributeValue();
    reader.close();
  }


  @Test
  public void validWithWhitespaces() throws IOException {
    String xml = "<  element    a = \"qwe\"  ></element>";
    XmlReader reader = readerFrom(xml);
    reader.beginElement();
    Assert.assertEquals("element", reader.nextElementName());
    Assert.assertEquals("a", reader.nextAttributeName());
    Assert.assertEquals("qwe", reader.nextAttributeValue());
    reader.close();
  }


  @Test
  @Ignore
  public void readObjectWithMultilineComment() {
    Assert.fail("Not implemented yet");

    String xml = "<element><!-- comment \n multiline \n --> Value</element>";
    XmlReader reader = readerFrom(xml);
  }
}
