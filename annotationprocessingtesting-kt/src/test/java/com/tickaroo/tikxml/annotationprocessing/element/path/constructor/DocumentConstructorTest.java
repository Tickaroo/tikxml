package com.tickaroo.tikxml.annotationprocessing.element.path.constructor;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import okio.Buffer;
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



    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, document);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document><toSkip><image/></toSkip></document>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    DocumentConstructor document2 = xml.read(TestUtils.sourceFrom(xmlStr), DocumentConstructor.class);
    Assert.assertEquals(document, document2);
  }

  @Test
  public void skipElementWithAttributesWithPath() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
    DocumentConstructor document =
        xml.read(TestUtils.sourceForFile("element_with_tag_with_attributes_to_skip_with_path.xml"), DocumentConstructor.class);
    Assert.assertNotNull(document);
    Assert.assertNotNull(document.getImage());


    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, document);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document><toSkip><image/></toSkip></document>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    DocumentConstructor document2 = xml.read(TestUtils.sourceFrom(xmlStr), DocumentConstructor.class);
    Assert.assertEquals(document, document2);

  }

  @Test
  public void skipElementWithPathDataClass() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
    DocumentConstructorDataClass document =
            xml.read(TestUtils.sourceForFile("element_with_tag_to_skip_with_path.xml"), DocumentConstructorDataClass.class);
    Assert.assertNotNull(document);
    Assert.assertNotNull(document.getImage());


    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, document);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document><toSkip><image/></toSkip></document>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    DocumentConstructorDataClass document2 = xml.read(TestUtils.sourceFrom(xmlStr), DocumentConstructorDataClass.class);
    Assert.assertEquals(document, document2);
  }

  @Test
  public void skipElementWithAttributesWithPathDataClass() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
    DocumentConstructorDataClass document =
            xml.read(TestUtils.sourceForFile("element_with_tag_with_attributes_to_skip_with_path.xml"), DocumentConstructorDataClass.class);
    Assert.assertNotNull(document);
    Assert.assertNotNull(document.getImage());


    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, document);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document><toSkip><image/></toSkip></document>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    DocumentConstructorDataClass document2 = xml.read(TestUtils.sourceFrom(xmlStr), DocumentConstructorDataClass.class);
    Assert.assertEquals(document, document2);

  }
}
