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

package com.tickaroo.tikxml.annotationprocessing.element.polymorphism.constructor;

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.TestUtils;
import java.io.IOException;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class WriterConstructorTest {


  @Test
  public void test() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    PaperConstructor paper = xml.read(TestUtils.sourceForFile("writer_journalist.xml"), PaperConstructor.class);


    Assert.assertNotNull(paper.getWriter());
    Assert.assertTrue(paper.getWriter() instanceof JournalistConstructor);
    JournalistConstructor writer = (JournalistConstructor) paper.getWriter();

    Assert.assertEquals("Hannes", writer.getName());
    Assert.assertEquals(40, writer.getAge());


    PaperConstructor paper2 = xml.read(TestUtils.sourceForFile("writer_organisation.xml"), PaperConstructor.class);


    Assert.assertNotNull(paper2.getWriter());
    Assert.assertTrue(paper2.getWriter() instanceof OrganisationConstructor);
    OrganisationConstructor writer2 = (OrganisationConstructor) paper2.getWriter();

    Assert.assertEquals("NY Times", writer2.getName());
    Assert.assertEquals("Foo Road 42", writer2.getAddress());


  }
}
