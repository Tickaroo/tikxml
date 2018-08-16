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

package com.tickaroo.tikxml.annotationprocessing.element.polymorphism;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class WriterTest {


  @Test
  public void test() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    Paper paperJournalist = xml.read(TestUtils.sourceForFile("writer_journalist.xml"), Paper.class);


    Assert.assertNotNull(paperJournalist.getWriter());
    Assert.assertTrue(paperJournalist.getWriter() instanceof Journalist);
    Journalist writer = (Journalist) paperJournalist.getWriter();

    Assert.assertEquals("Hannes", writer.getName());
    Assert.assertEquals(40, writer.getAge());


    Paper paperOrganisation = xml.read(TestUtils.sourceForFile("writer_organisation.xml"), Paper.class);


    Assert.assertNotNull(paperOrganisation.getWriter());
    Assert.assertTrue(paperOrganisation.getWriter() instanceof Organisation);
    Organisation writer2 = (Organisation) paperOrganisation.getWriter();

    Assert.assertEquals("NY Times", writer2.getName());
    Assert.assertEquals("Foo Road 42", writer2.getAddress());



    Buffer buffer = new Buffer();
    xml.write(buffer, paperJournalist);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><paper><journalist><name>Hannes</name><age>40</age></journalist></paper>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    Paper paperJournalist2 = xml.read(TestUtils.sourceFrom(xmlStr), Paper.class);
    Assert.assertEquals(paperJournalist, paperJournalist2);


    Buffer buffer2 = new Buffer();
    xml.write(buffer2, paperOrganisation);

    String xmlStr2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><paper><organisation><address>Foo Road 42</address><name>NY Times</name></organisation></paper>";
    Assert.assertEquals(xmlStr2, TestUtils.bufferToString(buffer2));

    Paper paperOrganisation2 = xml.read(TestUtils.sourceFrom(xmlStr2), Paper.class);
    Assert.assertEquals(paperOrganisation, paperOrganisation2);
  }

  @Test
  public void testDataClass() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    PaperDataClass paperJournalist = xml.read(TestUtils.sourceForFile("writer_journalist.xml"), PaperDataClass.class);


    Assert.assertNotNull(paperJournalist.getWriter());
    Assert.assertTrue(paperJournalist.getWriter() instanceof JournalistDataClass);
    JournalistDataClass writer = (JournalistDataClass) paperJournalist.getWriter();

    Assert.assertEquals("Hannes", writer.getName());
    Assert.assertEquals(40, writer.getAge());


    PaperDataClass paperOrganisation = xml.read(TestUtils.sourceForFile("writer_organisation.xml"), PaperDataClass.class);


    Assert.assertNotNull(paperOrganisation.getWriter());
    Assert.assertTrue(paperOrganisation.getWriter() instanceof OrganisationDataClass);
    OrganisationDataClass writer2 = (OrganisationDataClass) paperOrganisation.getWriter();

    Assert.assertEquals("NY Times", writer2.getName());
    Assert.assertEquals("Foo Road 42", writer2.getAddress());


    Buffer buffer = new Buffer();
    xml.write(buffer, paperJournalist);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><paper><journalist><name>Hannes</name><age>40</age></journalist></paper>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    PaperDataClass paperJournalist2 = xml.read(TestUtils.sourceFrom(xmlStr), PaperDataClass.class);
    Assert.assertEquals(paperJournalist, paperJournalist2);


    Buffer buffer2 = new Buffer();
    xml.write(buffer2, paperOrganisation);

    String xmlStr2 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><paper><organisation><address>Foo Road 42</address><name>NY Times</name></organisation></paper>";
    Assert.assertEquals(xmlStr2, TestUtils.bufferToString(buffer2));

    PaperDataClass paperOrganisation2 = xml.read(TestUtils.sourceFrom(xmlStr2), PaperDataClass.class);
    Assert.assertEquals(paperOrganisation, paperOrganisation2);
  }

}
