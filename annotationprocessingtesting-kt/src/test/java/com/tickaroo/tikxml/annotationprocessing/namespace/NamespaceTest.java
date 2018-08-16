package com.tickaroo.tikxml.annotationprocessing.namespace;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;

import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class NamespaceTest {

  @Test
  public void ignoreNamespaceDefinitions() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    Root item =
        xml.read(TestUtils.sourceForFile("namespaces.xml"), Root.class);

    Assert.assertEquals("Foo", item.getName());
    Assert.assertEquals(123, item.getChild().getId());
  }

  @Test
  public void namespaceDefaultDefinitions() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    RootDefaultNamespace item =
            xml.read(TestUtils.sourceForFile("regression/namespaces_default_fix.xml"), RootDefaultNamespace.class);

    Assert.assertEquals("Foo", item.getName());
  }

  @Test
  public void generateXmlWithDefaultNamespace() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    RootDefaultNamespace item = new RootDefaultNamespace();
    item.setName("Foo");
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rootDefaultNamespace xmlns=\"http://www.w3.org/1998/Math/MathML\"><name>Foo</name></rootDefaultNamespace>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    RootDefaultNamespace item2 =
            xml.read(TestUtils.sourceFrom(xmlStr), RootDefaultNamespace.class);
    Assert.assertEquals(item.getName(), item2.getName());
  }

  @Test
  public void ignoreNamespaceDefinitionsDataClass() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    RootDataClass item = xml.read(TestUtils.sourceForFile("namespaces.xml"), RootDataClass.class);

    Assert.assertEquals("Foo", item.getName());
    Assert.assertEquals(123, item.getChild().getId());
  }

  @Test
  public void namespaceDefaultDefinitionsDataClass() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    RootDefaultNamespaceDataClass item =
            xml.read(TestUtils.sourceForFile("regression/namespaces_default_fix.xml"), RootDefaultNamespaceDataClass.class);

    Assert.assertEquals("Foo", item.getName());
  }

  @Test
  public void generateXmlWithDefaultNamespaceDataClass() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    RootDefaultNamespaceDataClass item = new RootDefaultNamespaceDataClass();
    item.setName("Foo");
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rootDefaultNamespace xmlns=\"http://www.w3.org/1998/Math/MathML\"><name>Foo</name></rootDefaultNamespace>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    RootDefaultNamespaceDataClass item2 =
            xml.read(TestUtils.sourceFrom(xmlStr), RootDefaultNamespaceDataClass.class);
    Assert.assertEquals(item.getName(), item2.getName());

  }
}
