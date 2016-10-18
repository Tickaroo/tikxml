package com.tickaroo.tikxml;

import java.io.IOException;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class XmlWriterTest {

  @Test
  public void throwErrorIfMultipleRootElements() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("first");
    writer.endElement();

    try {
      writer.beginElement("second");
      Assert.fail("Execption expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "A xml document can only have one root xml element. There is already one but you try to add another one <second>",
          e.getMessage());
    }
  }

  @Test
  public void closeIncompleteDocument() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("first");

    try {
      writer.close();
      Assert.fail("Execption expected");
    } catch (IOException e) {
      Assert.assertEquals("Incomplete document. Abrupt end at /first in scope ELEMENT_OPENING",
          e.getMessage());
    }
  }

  @Test
  public void stringAttribute() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("e")
        .attribute("foo", "other")
        .endElement()
        .close();

    Assert.assertEquals("<e foo=\"other\"/>", TestUtils.bufferToString(buffer));
  }

  @Test
  public void intAttribute() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("e")
        .attribute("foo", Integer.MAX_VALUE - 1)
        .endElement()
        .close();

    Assert.assertEquals("<e foo=\"" + (Integer.MAX_VALUE - 1) + "\"/>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void longAttribute() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("e")
        .attribute("foo", Long.MAX_VALUE - 1)
        .endElement()
        .close();

    Assert.assertEquals("<e foo=\"" + (Long.MAX_VALUE - 1) + "\"/>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void doubleAttribute() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("e")
        .attribute("foo", Double.MAX_VALUE - 1 + 0.23)
        .endElement()
        .close();

    Assert.assertEquals("<e foo=\"" + (Double.MAX_VALUE - 1 + 0.23) + "\"/>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void booleanAttribute() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("e")
        .attribute("foo", true)
        .endElement()
        .close();

    Assert.assertEquals("<e foo=\"true\"/>",
        TestUtils.bufferToString(buffer));

    Buffer buffer2 = new Buffer();
    XmlWriter writer2 = XmlWriter.of(buffer2);

    writer2.beginElement("e")
        .attribute("foo", false)
        .endElement()
        .close();

    Assert.assertEquals("<e foo=\"false\"/>",
        TestUtils.bufferToString(buffer2));
  }

  @Test
  public void throwExceptionWhenAttributeInEmptyDocument() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    try {
      writer.attribute("foo", "other");
      Assert.fail("Execption expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Error while trying to write attribute foo=\"other\". Attributes can only be written in a opening xml element but was in xml scope EMPTY_DOCUMENT at path /",
          e.getMessage());
    }
  }

  @Test
  public void throwExceptionWhenAttributeInTextContent() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("root")
        .beginElement("foo")
        .endElement();
    try {
      writer.attribute("foo", "other");
      Assert.fail("Execption expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Error while trying to write attribute foo=\"other\". Attributes can only be written in a opening xml element but was in xml scope ELEMENT_CONTENT at path /root/text()",
          e.getMessage());
    }
  }

  @Test
  public void validTextContent() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("root")
        .textContent("Hello root")
        .beginElement("a")
        .textContent("Hello a")
        .beginElement("b")
        .attribute("att", "123")
        .textContent("Hello b")
        .endElement()
        .endElement()
        .endElement()
        .close();

    Assert.assertEquals("<root>Hello root<a>Hello a<b att=\"123\">Hello b</b></a></root>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void validTextContentCData() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("root")
        .textContent("Hello root")
        .beginElement("a")
        .textContentAsCData("Hello a")
        .beginElement("b")
        .attribute("att", "123")
        .textContentAsCData("Hello b")
        .endElement()
        .endElement()
        .endElement()
        .close();

    Assert.assertEquals(
        "<root>Hello root<a><![CDATA[Hello a]]><b att=\"123\"><![CDATA[Hello b]]></b></a></root>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void validTextContentInteger() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("root")
        .textContent(1)
        .beginElement("a")
        .textContent(2)
        .beginElement("b")
        .attribute("att", "123")
        .textContent(3)
        .endElement()
        .endElement()
        .endElement()
        .close();

    Assert.assertEquals("<root>1<a>2<b att=\"123\">3</b></a></root>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void validTextContentLong() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("root")
        .textContent(1L)
        .beginElement("a")
        .textContent(2L)
        .beginElement("b")
        .attribute("att", "123")
        .textContent(3L)
        .endElement()
        .endElement()
        .endElement()
        .close();

    Assert.assertEquals("<root>1<a>2<b att=\"123\">3</b></a></root>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void validTextContentDouble() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("root")
        .textContent(1.1)
        .beginElement("a")
        .textContent(2.2)
        .beginElement("b")
        .attribute("att", "123")
        .textContent(3.3)
        .endElement()
        .endElement()
        .endElement()
        .close();

    Assert.assertEquals("<root>1.1<a>2.2<b att=\"123\">3.3</b></a></root>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void validTextContentBoolean() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    writer.beginElement("root")
        .textContent(true)
        .beginElement("a")
        .textContent(false)
        .beginElement("b")
        .attribute("att", "123")
        .textContent(true)
        .endElement()
        .endElement()
        .endElement()
        .close();

    Assert.assertEquals("<root>true<a>false<b att=\"123\">true</b></a></root>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void throwExceptionWhenTextContentInEmptyDocument() {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    try {
      writer.textContent("foo");
      Assert.fail("Exception expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Error while trying to write text content \"foo\". Xml scope was EMPTY_DOCUMENT at path /",
          e.getMessage());
    }
  }

  @Test
  public void throwExceptionWhenTextContentCDataInEmptyDocument() {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    try {
      writer.textContentAsCData("foo");
      Assert.fail("Exception expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Error while trying to write text content \"foo\". Xml scope was EMPTY_DOCUMENT at path /",
          e.getMessage());
    }
  }

  @Test
  public void throwExceptionWhenTextContentInNonEmptyDocument() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("a")
        .endElement();

    try {
      writer.textContent("foo");
      Assert.fail("Exception expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Error while trying to write text content \"foo\". Xml scope was NONEMPTY_DOCUMENT at path /",
          e.getMessage());
    }
  }

  @Test
  public void throwExceptionWhenTextContentCDataInNonEmptyDocument() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("a")
        .endElement();

    try {
      writer.textContentAsCData("foo");
      Assert.fail("Exception expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Error while trying to write text content \"foo\". Xml scope was NONEMPTY_DOCUMENT at path /",
          e.getMessage());
    }
  }

  @Test
  public void endElementTooMuch() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("a")
        .endElement();

    try {
      writer.endElement();
      Assert.fail("Exception expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Trying to close the xml element, but all xml elements are already closed properly. Xml scope is NONEMPTY_DOCUMENT at path /",
          e.getMessage());
    }
  }

  @Test
  public void deepHierarchyCausesStackArrayCopy() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("root");

    int elementsCount = 70;

    for (int i = 0; i < elementsCount; i++) {
      writer.beginElement("e" + i);
    }

    for (int i = 0; i < elementsCount; i++) {
      writer.endElement();
    }

    writer.endElement();
    writer.close();
    String xml =
        "<root><e0><e1><e2><e3><e4><e5><e6><e7><e8><e9><e10><e11><e12><e13><e14><e15><e16><e17><e18><e19><e20><e21><e22><e23><e24><e25><e26><e27><e28><e29><e30><e31><e32><e33><e34><e35><e36><e37><e38><e39><e40><e41><e42><e43><e44><e45><e46><e47><e48><e49><e50><e51><e52><e53><e54><e55><e56><e57><e58><e59><e60><e61><e62><e63><e64><e65><e66><e67><e68><e69/></e68></e67></e66></e65></e64></e63></e62></e61></e60></e59></e58></e57></e56></e55></e54></e53></e52></e51></e50></e49></e48></e47></e46></e45></e44></e43></e42></e41></e40></e39></e38></e37></e36></e35></e34></e33></e32></e31></e30></e29></e28></e27></e26></e25></e24></e23></e22></e21></e20></e19></e18></e17></e16></e15></e14></e13></e12></e11></e10></e9></e8></e7></e6></e5></e4></e3></e2></e1></e0></root>";
    Assert.assertEquals(xml, TestUtils.bufferToString(buffer));
  }

  @Test
  public void validXmlDeclaration() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .xmlDeclaration()
        .beginElement("root")
        .beginElement("foo")
        .attribute("other", 123)
        .textContentAsCData("Hello<>wold")
        .endElement()
        .endElement();

    Assert.assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><foo other=\"123\"><![CDATA[Hello<>wold]]></foo></root>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void xmlDeclarationTwice() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .xmlDeclaration();
    try {
      writer.xmlDeclaration();
      Assert.fail("Exception expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Xml declaration <?xml version=\"1.0\" encoding=\"UTF-8\"?> has already been written in this xml document. Xml declaration can only be written once at the beginning of the document.",
          e.getMessage());
    }
  }

  @Test
  public void xmlDeclarationInBeginElementScope() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("foo");
    try {
      writer.xmlDeclaration();
      Assert.fail("Exception expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Xml Declatraion <?xml version=\"1.0\" encoding=\"UTF-8\"?> can only be written at the beginning of a xml document! You are not at the beginning of a xml document: current xml scope is ELEMENT_OPENING at path /foo",
          e.getMessage());
    }
  }

  @Test
  public void xmlDeclarationInTextContentScope() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("foo")
        .textContent("hello");

    try {
      writer.xmlDeclaration();
      Assert.fail("Exception expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Xml Declatraion <?xml version=\"1.0\" encoding=\"UTF-8\"?> can only be written at the beginning of a xml document! You are not at the beginning of a xml document: current xml scope is ELEMENT_CONTENT at path /foo/text()",
          e.getMessage());
    }
  }

  @Test
  public void xmlDeclarationInNonEmptyDocumentScope() throws IOException {
    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("foo")
        .textContent("hello")
        .endElement();

    try {
      writer.xmlDeclaration();
      Assert.fail("Exception expected");
    } catch (IOException e) {
      Assert.assertEquals(
          "Xml Declatraion <?xml version=\"1.0\" encoding=\"UTF-8\"?> can only be written at the beginning of a xml document! You are not at the beginning of a xml document: current xml scope is NONEMPTY_DOCUMENT at path /",
          e.getMessage());
    }
  }

  @Test
  public void validNamespaceWithPrefix() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("root")
        .namespace("http://example.com")
        .beginElement("a")
        .namespace(null, "http://foo.com")
        .endElement()
        .beginElement("b")
        .namespace("", "http://other.com")
        .attribute("att", 1)
        .beginElement("c")
        .attribute("att", 2.23)
        .namespace("m", "http://mobile.com")
        .endElement()
        .endElement()
        .endElement();

    Assert.assertEquals(
        "<root xmlns=\"http://example.com\"><a xmlns=\"http://foo.com\"/><b xmlns=\"http://other.com\" att=\"1\"><c att=\"2.23\" xmlns:m=\"http://mobile.com\"/></b></root>",
        TestUtils.bufferToString(buffer));
  }

  @Test
  public void throwExceptionBecauseNamespaceInEmptyDocuentScope() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer);

    try {
      writer.namespace("m", "http://foo.com");
    } catch (IOException e) {
      Assert.assertEquals("Error while trying to write attribute xmlns:m=\"http://foo.com\". Attributes can only be written in a opening xml element but was in xml scope EMPTY_DOCUMENT at path /", e.getMessage());
    }
  }

  @Test
  public void throwExceptionBecauseNamespaceInTextContentScope() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("a")
        .textContent("some text");

    try {
      writer.namespace("m", "http://foo.com");
    } catch (IOException e) {
      Assert.assertEquals("Error while trying to write attribute xmlns:m=\"http://foo.com\". Attributes can only be written in a opening xml element but was in xml scope ELEMENT_CONTENT at path /a/text()", e.getMessage());
    }
  }

  @Test
  public void throwExceptionBecauseNamespaceInNonEmptyDocimentScope() throws IOException {

    Buffer buffer = new Buffer();
    XmlWriter writer = XmlWriter.of(buffer)
        .beginElement("a")
        .textContent("some text")
        .endElement();

    try {
      writer.namespace("m", "http://foo.com");
    } catch (IOException e) {
      Assert.assertEquals("Error while trying to write attribute xmlns:m=\"http://foo.com\". Attributes can only be written in a opening xml element but was in xml scope NONEMPTY_DOCUMENT at path /", e.getMessage());
    }
  }
}
