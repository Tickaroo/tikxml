package com.tickaroo.tikxml.annotationprocessing.elementlist;

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.TestUtils;
import java.io.IOException;
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
  }
}
