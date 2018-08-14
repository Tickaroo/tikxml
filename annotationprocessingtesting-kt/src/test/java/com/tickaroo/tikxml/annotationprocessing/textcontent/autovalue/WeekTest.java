package com.tickaroo.tikxml.annotationprocessing.textcontent.autovalue;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import java.text.ParseException;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class WeekTest {

  @Test
  public void textContent() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    Week week = xml.read(TestUtils.sourceForFile("textcontent_week.xml"), Week.class);
    Assert.assertEquals(7, week.days().size());
    Assert.assertEquals("Monday", week.days().get(0).name());
    Assert.assertEquals("Tuesday", week.days().get(1).name());
    Assert.assertEquals("Wednesday", week.days().get(2).name());
    Assert.assertEquals("Thursday", week.days().get(3).name());
    Assert.assertEquals("Friday", week.days().get(4).name());
    Assert.assertEquals("Saturday", week.days().get(5).name());
    Assert.assertEquals("Sunday", week.days().get(6).name());

    // Writing xml test

    Buffer buffer = new Buffer();
    xml.write(buffer, week);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><week><day>Monday</day><day>Tuesday</day><day>Wednesday</day><day>Thursday</day><day>Friday</day><day>Saturday</day><day>Sunday</day></week>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));
    Week week2 = xml.read(TestUtils.sourceFrom(xmlStr), Week.class);
    Assert.assertEquals(week, week2);
  }
}
