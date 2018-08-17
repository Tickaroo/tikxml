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

package com.tickaroo.tikxml.annotationprocessing.propertyelement;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import java.text.ParseException;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class EmptyPropertyElementTest {

  @Test
  public void emptyStringForEmptyTag() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyStringPropertyElement item =
        xml.read(TestUtils.sourceForFile("empty_property_tag.xml"),
            EmptyStringPropertyElement.class);
    Assert.assertEquals("", item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty></empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyStringPropertyElement item2 =
        xml.read(TestUtils.sourceFrom(xmlStr), EmptyStringPropertyElement.class);
    Assert.assertEquals(item, item2);
  }

  @Test
  public void zeroIntForEmptyTag() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyIntPropertyElement item =
        xml.read(TestUtils.sourceForFile("empty_property_tag.xml"), EmptyIntPropertyElement.class);
    Assert.assertEquals(0, item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty>0</empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyIntPropertyElement item2 =
        xml.read(TestUtils.sourceFrom(xmlStr), EmptyIntPropertyElement.class);
    Assert.assertEquals(item, item2);
  }

  @Test
  public void zeroLongForEmptyTag() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyLongPropertyElement item =
        xml.read(TestUtils.sourceForFile("empty_property_tag.xml"), EmptyLongPropertyElement.class);
    Assert.assertEquals(0, item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty>0</empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyLongPropertyElement item2 =
        xml.read(TestUtils.sourceFrom(xmlStr), EmptyLongPropertyElement.class);
    Assert.assertEquals(item, item2);
  }

  @Test
  public void zeroDoubleForEmptyTag() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyDoublePropertyElement item =
        xml.read(TestUtils.sourceForFile("empty_property_tag.xml"),
            EmptyDoublePropertyElement.class);
    Assert.assertEquals(0, 0, item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty>0.0</empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyDoublePropertyElement item2 =
        xml.read(TestUtils.sourceFrom(xmlStr), EmptyDoublePropertyElement.class);
    Assert.assertEquals(item, item2);
  }

  @Test
  public void falseBooleanForEmptyTag() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyBooleanPropertyElement item =
        xml.read(TestUtils.sourceForFile("empty_property_tag.xml"),
            EmptyBooleanPropertyElement.class);
    Assert.assertEquals(false, item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty>false</empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyBooleanPropertyElement item2 =
        xml.read(TestUtils.sourceFrom(xmlStr), EmptyBooleanPropertyElement.class);
    Assert.assertEquals(item, item2);
  }


  @Test
  public void emptyStringForEmptyTagDataClass() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyStringPropertyElementDataClass item =
            xml.read(TestUtils.sourceForFile("empty_property_tag.xml"),
                    EmptyStringPropertyElementDataClass.class);
    Assert.assertEquals("", item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty></empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyStringPropertyElementDataClass item2 =
            xml.read(TestUtils.sourceFrom(xmlStr), EmptyStringPropertyElementDataClass.class);
    Assert.assertEquals(item, item2);
  }

  @Test
  public void zeroIntForEmptyTagDataClass() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyIntPropertyElementDataClass item =
            xml.read(TestUtils.sourceForFile("empty_property_tag.xml"), EmptyIntPropertyElementDataClass.class);
    Assert.assertEquals(0, item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty>0</empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyIntPropertyElementDataClass item2 =
            xml.read(TestUtils.sourceFrom(xmlStr), EmptyIntPropertyElementDataClass.class);
    Assert.assertEquals(item, item2);
  }

  @Test
  public void zeroLongForEmptyTagDataClass() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyLongPropertyElementDataClass item =
            xml.read(TestUtils.sourceForFile("empty_property_tag.xml"), EmptyLongPropertyElementDataClass.class);
    Assert.assertEquals(0, item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty>0</empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyLongPropertyElementDataClass item2 =
            xml.read(TestUtils.sourceFrom(xmlStr), EmptyLongPropertyElementDataClass.class);
    Assert.assertEquals(item, item2);
  }

  @Test
  public void zeroDoubleForEmptyTagDataClass() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyDoublePropertyElementDataClass item =
            xml.read(TestUtils.sourceForFile("empty_property_tag.xml"),
                    EmptyDoublePropertyElementDataClass.class);
    Assert.assertEquals(0, 0, item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty>0.0</empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyDoublePropertyElementDataClass item2 =
            xml.read(TestUtils.sourceFrom(xmlStr), EmptyDoublePropertyElementDataClass.class);
    Assert.assertEquals(item, item2);
  }

  @Test
  public void falseBooleanForEmptyTagDataClass() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    EmptyBooleanPropertyElementDataClass item =
            xml.read(TestUtils.sourceForFile("empty_property_tag.xml"),
                    EmptyBooleanPropertyElementDataClass.class);
    Assert.assertEquals(false, item.getEmpty());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, item);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyPropertyTag><empty>false</empty></emptyPropertyTag>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    EmptyBooleanPropertyElementDataClass item2 =
            xml.read(TestUtils.sourceFrom(xmlStr), EmptyBooleanPropertyElementDataClass.class);
    Assert.assertEquals(item, item2);
  }
}