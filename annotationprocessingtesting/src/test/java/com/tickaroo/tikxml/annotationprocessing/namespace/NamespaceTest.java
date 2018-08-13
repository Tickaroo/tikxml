package com.tickaroo.tikxml.annotationprocessing.namespace;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import org.junit.*;
import java.text.ParseException;
import okio.Buffer;

/**
 * @author Hannes Dorfmann
 */
public class NamespaceTest {

  @Test
  public void ignoreNamespaceDefinitions() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    Root item =
        xml.read(TestUtils.sourceForFile("namespaces.xml"), Root.class);

    Assert.assertEquals("Foo", item.name);
    Assert.assertEquals(123, item.child.id);
  }

  @Test
  public void namespaceDefaultDefinitions() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    RootDefaultNamespace item =
            xml.read(TestUtils.sourceForFile("regression/namespaces_default_fix.xml"), RootDefaultNamespace.class);

    Assert.assertEquals("Foo", item.name);
  }

  @Test
  public void generateXmlWithDefaultNamespace() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    RootDefaultNamespace item = new RootDefaultNamespace();
    item.name = "Foo";
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rootDefaultNamespace xmlns=\"http://www.w3.org/1998/Math/MathML\"><name>Foo</name></rootDefaultNamespace>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    RootDefaultNamespace item2 =
            xml.read(TestUtils.sourceFrom(xmlStr), RootDefaultNamespace.class);
    Assert.assertEquals(item.name, item2.name);

  }
}
