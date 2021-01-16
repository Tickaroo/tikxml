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

package com.tickaroo.tikxml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class TikXmlBuilderTest {



  @Test
  public void throwExceptionOnMissingMappingTest() {

    TikXml tikXml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
    Assert.assertFalse(tikXml.config.exceptionOnUnreadXml);


    TikXml tikXml2 = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    Assert.assertTrue(tikXml2.config.exceptionOnUnreadXml);
  }

  @Test
  public void addTypeAdapter() throws IOException {

    TypeConvertersTest.TestConverter converter = new TypeConvertersTest.TestConverter();

    TikXml tikXml = new TikXml.Builder()
        .addTypeConverter(Object.class, converter)
        .build();

    Assert.assertSame(converter, tikXml.config.getTypeConverter(Object.class));
  }

  @Test
  public void defaultCharsetUTF8() {
    TikXml tikXml = new TikXml.Builder().build();

    Assert.assertEquals(StandardCharsets.UTF_8, tikXml.config.charset());
  }

  @Test
  public void charset() {
    TikXml tikXml = new TikXml.Builder()
            .charset(StandardCharsets.ISO_8859_1)
            .build();

    Assert.assertEquals(StandardCharsets.ISO_8859_1, tikXml.config.charset());
  }
}
