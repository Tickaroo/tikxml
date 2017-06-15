package com.tickaroo.tikxml.annotationprocessing.elementlist;

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.TestUtils;
import java.io.IOException;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class EmptyTagListTest {

  @Test
  public void test() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    EmptyTagList emptyTagList =
        xml.read(TestUtils.sourceForFile("empty_tag_list.xml"), EmptyTagList.class);
    Assert.assertNotNull(emptyTagList);
    Assert.assertNotNull(emptyTagList.tags);
    Assert.assertEquals(3, emptyTagList.tags.size());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, emptyTagList);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyTagList><empty/><empty/><empty/></emptyTagList>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyTagList emptyTagList2 = xml.read(TestUtils.sourceFrom(xmlStr), EmptyTagList.class);
    Assert.assertEquals(emptyTagList, emptyTagList2);
  }
}
