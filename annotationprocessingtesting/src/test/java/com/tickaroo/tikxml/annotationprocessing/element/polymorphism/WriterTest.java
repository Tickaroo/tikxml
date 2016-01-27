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

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.TestUtils;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class WriterTest {


  @Test
  public void test() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    Paper paper = xml.read(TestUtils.sourceForFile("writer_journalist.xml"), Paper.class);


    Assert.assertNotNull(paper.writer);
    Assert.assertTrue(paper.writer instanceof Journalist);
    Journalist writer = (Journalist) paper.writer;

    Assert.assertEquals("Hannes", writer.name);
    Assert.assertEquals(40, writer.age);


    Paper paper2 = xml.read(TestUtils.sourceForFile("writer_organisation.xml"), Paper.class);


    Assert.assertNotNull(paper2.writer);
    Assert.assertTrue(paper2.writer instanceof Organisation);
    Organisation writer2 = (Organisation) paper2.writer;

    Assert.assertEquals("NY Times", writer2.name);
    Assert.assertEquals("Foo Road 42", writer2.address);


  }
}
