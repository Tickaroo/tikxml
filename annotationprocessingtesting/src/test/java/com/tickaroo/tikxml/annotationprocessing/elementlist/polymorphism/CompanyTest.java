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

package com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import java.text.ParseException;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class CompanyTest {

  @Test
  public void simple() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    Company company = xml.read(TestUtils.sourceForFile("company.xml"), Company.class);

    Assert.assertEquals("Tickaroo", company.name);
    Assert.assertEquals(4, company.persons.size());

    Assert.assertTrue(company.persons.get(0) instanceof Boss);

    Boss boss = (Boss) company.persons.get(0);
    Assert.assertEquals("Naomi", boss.firstName);
    Assert.assertEquals("Owusu", boss.lastName);

    Employee employee = (Employee) company.persons.get(1);
    Assert.assertEquals("Hannes", employee.name);


    employee = (Employee) company.persons.get(2);
    Assert.assertEquals("Lukas", employee.name);


    employee = (Employee) company.persons.get(3);
    Assert.assertEquals("Bodo", employee.name);

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, company);

    String xmlStr =
       "<?xml version=\"1.0\" encoding=\"UTF-8\"?><company><boss firstName=\"Naomi\" lastName=\"Owusu\"/><employee><name>Hannes</name></employee><employee><name>Lukas</name></employee><employee><name>Bodo</name></employee><name>Tickaroo</name></company>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    Company company2 = xml.read(TestUtils.sourceFrom(xmlStr), Company.class);
    Assert.assertEquals(company, company2);
  }

}
