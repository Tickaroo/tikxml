package com.tickaroo.tikxml.annotationprocessing.textcontent.constructor;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import java.text.ParseException;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class WeekConstructorTest {

  @Test
  public void textContent() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    WeekConstructor week = xml.read(TestUtils.sourceForFile("textcontent_week.xml"), WeekConstructor.class);
    Assert.assertEquals(7, week.getDays().size());
    Assert.assertEquals("Monday", week.getDays().get(0).getName());
    Assert.assertEquals("Tuesday", week.getDays().get(1).getName());
    Assert.assertEquals("Wednesday", week.getDays().get(2).getName());
    Assert.assertEquals("Thursday", week.getDays().get(3).getName());
    Assert.assertEquals("Friday", week.getDays().get(4).getName());
    Assert.assertEquals("Saturday", week.getDays().get(5).getName());
    Assert.assertEquals("Sunday", week.getDays().get(6).getName());


    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, week);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><week><day>Monday</day><day>Tuesday</day><day>Wednesday</day><day>Thursday</day><day>Friday</day><day>Saturday</day><day>Sunday</day></week>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    WeekConstructor week2 = xml.read(TestUtils.sourceFrom(xmlStr), WeekConstructor.class);
    Assert.assertEquals(week, week2);
  }

}
