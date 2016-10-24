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

/**
 * @author Hannes Dorfmann
 */
public class SampleClass {

  int anInt;
  String aString;


  public static class $TypeAdapter implements TypeAdapter<SampleClass> {

    @Override
    public SampleClass fromXml(XmlReader reader, TikXmlConfig config) {
      return null;
    }

    @Override
    public void toXml(XmlWriter writer, TikXmlConfig config, SampleClass value, String overridingXmlElementTagName) {

    }
  }

}