package com.tickaroo.tikxml.annotationprocessing.namespace;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
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

    Assert.assertEquals("Foo", item.name);
    Assert.assertEquals(123, item.child.id);
  }
}
