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
import java.util.Date;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Hannes Dorfmann
 */
public class TypeConvertersTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  static class TestConverter implements TypeConverter<Object> {
    @Override
    public Object read(String value) throws Exception {
      return null;
    }

    @Override
    public String write(Object value) throws Exception {
      return null;
    }
  }

  @Test
  public void addAndGetTypeConverter() throws IOException {

    TypeConverters converters = new TypeConverters();

    exception.expect(IOException.class);
    converters.get(TestConverter.class);

    TestConverter converter = new TestConverter();

    converters.add(Object.class, converter);
    Assert.assertTrue(converter == converters.get(Object.class));

    exception.expect(IOException.class);
    converters.get(Date.class);


  }
}
