package com.tickaroo.tikxml.annotationprocessing.element.path.constructor;

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.TestUtils;
import java.io.IOException;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class DocumentConstructorTest {

  @Test
  public void skipElementWithPath() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
    DocumentConstructor document =
        xml.read(TestUtils.sourceForFile("element_with_tag_to_skip_with_path.xml"), DocumentConstructor.class);
    Assert.assertNotNull(document);
    Assert.assertNotNull(document.getImage());
  }

  @Test
  public void skipElementWithAttributesWithPath() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
    DocumentConstructor document =
        xml.read(TestUtils.sourceForFile("element_with_tag_with_attributes_to_skip_with_path.xml"), DocumentConstructor.class);
    Assert.assertNotNull(document);
    Assert.assertNotNull(document.getImage());

  }
}
