package com.tickaroo.tikxml.annotationprocessing.element;

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.TestUtils;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class EmptyTagTest {

  @Test
  public void test() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    EmptyTag emptyTag = xml.read(TestUtils.sourceForFile("empty_tag.xml"), EmptyTag.class);
    Assert.assertNotNull(emptyTag);
  }
}
