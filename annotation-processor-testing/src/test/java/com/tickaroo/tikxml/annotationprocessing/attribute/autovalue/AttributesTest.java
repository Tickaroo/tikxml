/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tickaroo.tikxml.annotationprocessing.attribute.autovalue;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class AttributesTest {
  @Test
  public void autoValue() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    // Test reading xml
    ItemAV item =
        xml.read(TestUtils.sourceForFile("attributes.xml"), ItemAV.class);

    Date date = DateConverter.format.parse("1988-03-04");

    Assert.assertEquals("foo", item.aString());
    Assert.assertEquals(123, item.anInt());
    Assert.assertEquals(true, item.aBoolean());
    Assert.assertEquals(23.42, item.aDouble(), 0);
    Assert.assertEquals(2147483648L, item.aLong());
    Assert.assertEquals(date, item.aDate());

    Assert.assertEquals(123, (int) item.intWrapper());
    Assert.assertEquals(true, item.booleanWrapper());
    Assert.assertEquals(23.42, item.doubleWrapper(), 0);
    Assert.assertEquals(2147483648L, (long) item.longWrapper());

    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><item aBoolean=\"true\" longWrapper=\"2147483648\" aString=\"foo\" intWrapper=\"123\" aLong=\"2147483648\" anInt=\"123\" aDate=\"1988-03-04\" aDouble=\"23.42\" doubleWrapper=\"23.42\" booleanWrapper=\"true\"/>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    ItemAV item2 =
        xml.read(TestUtils.sourceFrom(xmlStr), ItemAV.class);
    Assert.assertEquals(item, item2);
  }
}
