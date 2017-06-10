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

package com.tickaroo.tikxml.regressiontests.paths;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import java.text.ParseException;
import okio.Buffer;
import org.junit.*;

/**
 * Skip some internal elements
 *
 * @author Hannes Dorfmann
 */
public class PolymorphicPathTest {

  @Test
  public void simple() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    Company company =
        xml.read(TestUtils.sourceForFile("regression/deep_polymprphic_paths.xml"), Company.class);

    Assert.assertEquals(company.persons.size(), 3);
    Boss boss = (Boss) company.persons.get(0);
    Employee employee = (Employee) company.persons.get(1);
    Person person = company.persons.get(2);

    Assert.assertEquals(boss.id, 1);
    Assert.assertEquals(boss.name, "Boss");
    Assert.assertEquals(employee.id, 2);
    Assert.assertEquals(employee.name, "Employee");
    Assert.assertEquals(person.id, 3);

    // Writing xml test

    Buffer buffer = new Buffer();
    xml.write(buffer, company);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><company><department><persons><boss><name>Boss</name><id>1</id></boss><employee><name>Employee</name><id>2</id></employee><person><id>3</id></person></persons></department></company>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    Company company2 = xml.read(TestUtils.sourceFrom(xmlStr), Company.class);
    Assert.assertEquals(company, company2);
  }
}
