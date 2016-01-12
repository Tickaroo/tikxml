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

package com.tickaroo.tikxml.typeadapter;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import okio.BufferedSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Hannes Dorfmann
 */
public class DelegatingTypeAdapterTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void readTest() throws IOException {
    TikXml tikXml = new TikXml.Builder().
        addTypeAdapter(Company.class, new CompanyDelegatingTypeAdapter())
        .build();

    BufferedSource source = TestUtils.sourceForFile("simple_typeadapater_test.xml");

    Company company = tikXml.read(source, Company.class);

    Assert.assertEquals(123, company.id);
    Assert.assertEquals("Foo Inc.", company.name);

  }

  @Test
  public void failUnmappedAttribute() throws IOException {
    TikXml tikXml = new TikXml.Builder()
        .throwExceptionOnMissingMapping(true)
        .addTypeAdapter(Company.class, new CompanyDelegatingTypeAdapterWithoutNameAttribute())
        .build();


    BufferedSource source = TestUtils.sourceForFile("simple_typeadapater_test.xml");

    exception.expect(IOException.class);
    exception.expectMessage("Could not map the xml attribute with the name 'name' to java class. Have you annotated such a field in your java class to map this xml attribute?");
    Company company = tikXml.read(source, Company.class);


  }


  @Test
  public void ignoreUnmappedAttribute() throws IOException {
    TikXml tikXml = new TikXml.Builder()
        .throwExceptionOnMissingMapping(false)
        .addTypeAdapter(Company.class, new CompanyDelegatingTypeAdapterWithoutNameAttribute())
        .build();


    BufferedSource source = TestUtils.sourceForFile("simple_typeadapater_test.xml");

    Company company = tikXml.read(source, Company.class);


    Assert.assertEquals(123, company.id);
    Assert.assertNull(company.name);


  }
}
