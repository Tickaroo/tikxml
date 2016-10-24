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

import com.tickaroo.tikxml.typeadapter.TypeAdapter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Hannes Dorfmann
 */
public class TypeAdaptersTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void addAndGetAdapter() throws IOException {
    TypeAdapters adapters = new TypeAdapters();

    exception.expect(TypeAdapterNotFoundException.class);
    adapters.get(Object.class);

    TestTypeAdapter adapter = new TestTypeAdapter();
    adapters.add(Object.class, adapter);
    Assert.assertSame(adapter, adapters.get(Object.class));
  }

  @Test
  public void instantiateOverReflections() throws IOException {
    TypeAdapters adapters = new TypeAdapters();
    TypeAdapter<SampleClass> adapter = adapters.get(SampleClass.class);
    Assert.assertTrue(adapter instanceof SampleClass.$TypeAdapter);
    Assert.assertSame(adapter, adapters.get(SampleClass.class));
  }


  class TestTypeAdapter implements TypeAdapter<Object> {

    @Override
    public Object fromXml(XmlReader reader, TikXmlConfig config) {
      return null;
    }

    @Override
    public void toXml(XmlWriter writer, TikXmlConfig config, Object value, String overridingXmlElementTagName) {

    }
  }
}
