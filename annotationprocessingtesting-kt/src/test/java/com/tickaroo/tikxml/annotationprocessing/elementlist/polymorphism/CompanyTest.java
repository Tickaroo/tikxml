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

    Assert.assertEquals("Tickaroo", company.getName());
    Assert.assertEquals(4, company.getPersons().size());

    Assert.assertTrue(company.getPersons().get(0) instanceof Boss);

    Boss boss = (Boss) company.getPersons().get(0);
    Assert.assertEquals("Naomi", boss.getFirstName());
    Assert.assertEquals("Owusu", boss.getLastName());

    Employee employee = (Employee) company.getPersons().get(1);
    Assert.assertEquals("Hannes", employee.getName());


    employee = (Employee) company.getPersons().get(2);
    Assert.assertEquals("Lukas", employee.getName());


    employee = (Employee) company.getPersons().get(3);
    Assert.assertEquals("Bodo", employee.getName());

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

  @Test
  public void simpleDataClass() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    CompanyDataClass company = xml.read(TestUtils.sourceForFile("company.xml"), CompanyDataClass.class);

    Assert.assertEquals("Tickaroo", company.getName());
    Assert.assertEquals(4, company.getPersons().size());

    Assert.assertTrue(company.getPersons().get(0) instanceof BossDataClass);

    BossDataClass boss = (BossDataClass) company.getPersons().get(0);
    Assert.assertEquals("Naomi", boss.getFirstName());
    Assert.assertEquals("Owusu", boss.getLastName());

    EmployeeDataClass employee = (EmployeeDataClass) company.getPersons().get(1);
    Assert.assertEquals("Hannes", employee.getName());


    employee = (EmployeeDataClass) company.getPersons().get(2);
    Assert.assertEquals("Lukas", employee.getName());


    employee = (EmployeeDataClass) company.getPersons().get(3);
    Assert.assertEquals("Bodo", employee.getName());

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, company);

    String xmlStr =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><company><boss firstName=\"Naomi\" lastName=\"Owusu\"/><employee><name>Hannes</name></employee><employee><name>Lukas</name></employee><employee><name>Bodo</name></employee><name>Tickaroo</name></company>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    CompanyDataClass company2 = xml.read(TestUtils.sourceFrom(xmlStr), CompanyDataClass.class);
    Assert.assertEquals(company, company2);
  }

}
