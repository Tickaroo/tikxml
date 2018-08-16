package com.tickaroo.tikxml.annotationprocessing.element;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;

import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class EmptyTagTest {

  @Test
  public void test() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    EmptyTag emptyTag = xml.read(TestUtils.sourceForFile("empty_tag.xml"), EmptyTag.class);
    Assert.assertNotNull(emptyTag);

    Buffer buffer = new Buffer();
    xml.write(buffer, emptyTag);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><empty/>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));
  }
}
