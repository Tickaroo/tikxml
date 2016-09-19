package com.tickaroo.tikxml.annotationprocessing.element.path;

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.TestUtils;
import java.io.IOException;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class DocumentTest {

  @Test
  public void skipElementWithPath() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
    Document document =
        xml.read(TestUtils.sourceForFile("element_with_tag_to_skip_with_path.xml"), Document.class);
    Assert.assertNotNull(document);
    Assert.assertNotNull(document.image);
  }

  @Test
  public void skipElementWithAttributesWithPath() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
    Document document =
        xml.read(TestUtils.sourceForFile("element_with_tag_with_attributes_to_skip_with_path.xml"), Document.class);
    Assert.assertNotNull(document);
    Assert.assertNotNull(document.image);

  }
}
