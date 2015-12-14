package com.tickaroo.tikxml;

import java.io.IOException;
import org.junit.Assert;
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

    String elementText = "I'm an ElementText\n with multiple lines and special \n chars @ \" üäöß?1§$%&&/()=*'";
    String xml = "<element a=\"qwe\" b='123' c=\"skipMe\">" + elementText + "</element>";
    XmlReader reader = readerFrom(xml);

    try {


      Assert.assertEquals(reader.peek(), XmlReader.XmlToken.ELEMENT_BEGIN);
      Assert.assertTrue(reader.hasElement());
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

      Assert.assertTrue(reader.hasTextContent());
      Assert.assertEquals(XmlReader.XmlToken.ELEMENT_TEXT_CONTENT, reader.peek());
      Assert.assertEquals(elementText, reader.nextTextContent());
      Assert.assertFalse(reader.hasTextContent());

      Assert.assertEquals(XmlReader.XmlToken.ELEMENT_END, reader.peek());
      reader.endElement();


      Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());

    } finally {
      reader.close();
    }
  }

  @Test
  public void attributeWithDoubleEqualsSign() throws IOException {
    String xml = "<element a==\"qwe\"></element>";
    XmlReader reader = readerFrom(xml);
    try {
      reader.beginElement();
      reader.nextElementName();
      reader.nextAttributeName();
      exception.expect(IOException.class);
      reader.nextAttributeValue();
    } finally {
      reader.close();
    }
  }


  @Test
  public void attributeNoQuotes() throws IOException {
    String xml = "<element a=qwe></element>";
    XmlReader reader = readerFrom(xml);

    try {
      reader.beginElement();
      reader.nextElementName();
      reader.nextAttributeName();
      exception.expect(IOException.class);
      reader.nextAttributeValue();

    } finally {
      reader.close();
    }

  }


  @Test
  public void validWithWhitespaces() throws IOException {
    String xml = "<  element    a = \"qwe\"  ></element>";
    XmlReader reader = readerFrom(xml);

    try {
      reader.beginElement();
      Assert.assertEquals("element", reader.nextElementName());
      Assert.assertEquals("a", reader.nextAttributeName());
      Assert.assertEquals("qwe", reader.nextAttributeValue());
      Assert.assertFalse(reader.hasTextContent());
      reader.endElement();
    } finally {
      reader.close();
    }
  }


  @Test
  public void readObjectWithMultilineComment() throws IOException {
    String xml = "<element><!-- comment \n multiline \n -->Value</element>";
    XmlReader reader = readerFrom(xml);

    try {
      Assert.assertTrue(reader.hasElement());
      reader.beginElement();
      Assert.assertEquals("element", reader.nextElementName());
      Assert.assertTrue(reader.hasTextContent());
      Assert.assertEquals(reader.nextTextContent(), "Value");
      reader.endElement();

      Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
    } finally {
      reader.close();
    }

  }

  @Test
  public void emptyTextContent() throws IOException {
    String xml = "<element></element>";
    XmlReader reader = readerFrom(xml);

    try {
      Assert.assertTrue(reader.hasElement());
      reader.beginElement();
      Assert.assertEquals("element", reader.nextElementName());
      Assert.assertFalse(reader.hasTextContent());
      reader.endElement();
      Assert.assertFalse(reader.hasElement());
      Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
    } finally {
      reader.close();
    }
  }


  @Test
  public void inlineClosing() throws IOException {
    String xml = "<element a='foo' />";
    XmlReader reader = readerFrom(xml);

    try {
      Assert.assertTrue(reader.hasElement());
      reader.beginElement();
      Assert.assertEquals("element", reader.nextElementName());
      Assert.assertTrue(reader.hasAttribute());
      Assert.assertEquals("a", reader.nextAttributeName());
      Assert.assertEquals("foo", reader.nextAttributeValue());

      Assert.assertEquals(XmlReader.XmlToken.ELEMENT_END, reader.peek());
      reader.endElement();
      Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());

    } finally {
      reader.close();
    }
  }


  @Test
  public void incompleteSingleQuoteAttribute() throws IOException {
    String xml = "<element a='foo />";
    XmlReader reader = readerFrom(xml);

    try {
      reader.hasElement();
      reader.beginElement();
      Assert.assertEquals("element", reader.nextElementName());
      Assert.assertTrue(reader.hasAttribute());
      Assert.assertEquals("a", reader.nextAttributeName());
      exception.expect(IOException.class);
      exception.expectMessage("Unterminated string (single quote ' is missing) at path /element[@a]");
      reader.nextAttributeValue();
    } finally {
      reader.close();
    }
  }


  @Test
  public void incompleteDoubleQuoteAttribute() throws IOException {
    String xml = "<element a=\"foo ></element>";
    XmlReader reader = readerFrom(xml);

    try {
      reader.hasElement();
      reader.beginElement();
      Assert.assertEquals("element", reader.nextElementName());
      Assert.assertTrue(reader.hasAttribute());
      Assert.assertEquals("a", reader.nextAttributeName());
      exception.expect(IOException.class);
      exception.expectMessage("Unterminated string (double quote \" is missing) at path /element[@a]");
      reader.nextAttributeValue();
    } finally {
      reader.close();
    }
  }


  @Test
  public void missingClosingTag() throws IOException {
    String xml = "<element a='foo' >";
    XmlReader reader = readerFrom(xml);
    try {
      Assert.assertTrue(reader.hasElement());
      reader.beginElement();
      Assert.assertEquals("element", reader.nextElementName());
      Assert.assertTrue(reader.hasAttribute());
      Assert.assertEquals("a", reader.nextAttributeName());
      Assert.assertEquals("foo", reader.nextAttributeValue());

      exception.expect(IOException.class);
      exception.expectMessage("Unexpected end of input at path /element/text()");
      reader.endElement();
    } finally {
      reader.close();
    }

  }


  @Test
  public void unclosedElement() throws IOException {
    String xml = "<fooElement a=\"qwe\">This is the text";
    XmlReader reader = readerFrom(xml);

    try {
      reader.beginElement();
      reader.nextElementName();
      reader.nextAttributeName();
      reader.nextAttributeValue();
      exception.expect(IOException.class);
      exception.expectMessage("Unterminated element text content. Expected </fooElement> but haven't found at path /fooElement/text()");
      reader.nextTextContent();

    } finally {
      reader.close();
    }
  }

  @Test
  public void noAttributesNoTextContent() throws IOException {
    String xml = "<foo></foo>";
    XmlReader reader = readerFrom(xml);

    try {
      Assert.assertTrue(reader.hasElement());
      reader.beginElement();
      Assert.assertEquals("foo", reader.nextElementName());
      Assert.assertFalse(reader.hasAttribute());
      Assert.assertFalse(reader.hasTextContent());
      reader.endElement();

      Assert.assertFalse(reader.hasElement());
      Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());

    } finally {
      reader.close();
    }
  }

  @Test
  public void noAttributesButTextContent() throws IOException {
    String xml = "<foo>Value</foo>";
    XmlReader reader = readerFrom(xml);

    try {
      Assert.assertTrue(reader.hasElement());
      reader.beginElement();
      Assert.assertEquals("foo", reader.nextElementName());
      Assert.assertFalse(reader.hasAttribute());
      Assert.assertTrue(reader.hasTextContent());
      Assert.assertEquals("Value", reader.nextTextContent());
      reader.endElement();

      Assert.assertFalse(reader.hasElement());
      Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());

    } finally {
      reader.close();
    }
  }


  @Test
  public void skipTextContent() throws IOException {
    String xml = "<foo>Value</foo>";
    XmlReader reader = readerFrom(xml);

    try {
      Assert.assertTrue(reader.hasElement());
      reader.beginElement();
      Assert.assertEquals("foo", reader.nextElementName());
      Assert.assertFalse(reader.hasAttribute());
      Assert.assertTrue(reader.hasTextContent());
      reader.skipTextContent();
      reader.endElement();

      Assert.assertFalse(reader.hasElement());
      Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());

    } finally {
      reader.close();
    }
  }

  @Test
  public void skipUncompleteTextContent() throws IOException {
    String xml = "<foo>Value";
    XmlReader reader = readerFrom(xml);

    try {
      Assert.assertTrue(reader.hasElement());
      reader.beginElement();
      Assert.assertEquals("foo", reader.nextElementName());
      Assert.assertFalse(reader.hasAttribute());
      Assert.assertTrue(reader.hasTextContent());
      exception.expect(IOException.class);
      exception.expectMessage("Unterminated element text content. Expected </foo> but haven't found at path /foo/text()");
      reader.skipTextContent();
    } finally {
      reader.close();
    }
  }

  @Test
  public void noElementName() throws IOException {
    String xml = "<></>";
    XmlReader reader = readerFrom(xml);

    try {
      Assert.assertTrue(reader.hasElement());
      reader.beginElement();

      exception.expect(IOException.class);
      exception.expectMessage("Expected xml element name (literal expected) at path /");
      reader.nextElementName();
    } finally {
      reader.close();
    }
  }

}
