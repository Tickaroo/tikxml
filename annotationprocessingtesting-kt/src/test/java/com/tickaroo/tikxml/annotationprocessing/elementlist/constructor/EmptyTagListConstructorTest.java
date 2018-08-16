package com.tickaroo.tikxml.annotationprocessing.elementlist.constructor;

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.TestUtils;
import java.io.IOException;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class EmptyTagListConstructorTest {

  @Test
  public void test() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    EmptyTagListConstructor emptyTagList =
        xml.read(TestUtils.sourceForFile("empty_tag_list.xml"), EmptyTagListConstructor.class);
    Assert.assertNotNull(emptyTagList);
    Assert.assertNotNull(emptyTagList.getTags());
    Assert.assertEquals(3, emptyTagList.getTags().size());
  }

  @Test
  public void testDataClass() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    EmptyTagListConstructorDataClass emptyTagList =
            xml.read(TestUtils.sourceForFile("empty_tag_list.xml"), EmptyTagListConstructorDataClass.class);
    Assert.assertNotNull(emptyTagList);
    Assert.assertNotNull(emptyTagList.getTags());
    Assert.assertEquals(3, emptyTagList.getTags().size());
  }
}
