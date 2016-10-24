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

import com.tickaroo.tikxml.TikXmlConfig;
import com.tickaroo.tikxml.TypeConverter;
import com.tickaroo.tikxml.XmlReader;
import com.tickaroo.tikxml.XmlWriter;
import java.io.IOException;

/**
 * As the name already suggests, a type adapter is responsible to convert a xml element to a java
 * object and vice versa. For simple "primitive alike" data classes like "java.util.Date" you can
 * use {@link TypeConverter}
 *
 * <p> {@link TypeAdapter}s might be instantiated via reflections {@code class.newInstance()} call.
 * Therefore, {@link TypeAdapter} must provide an empty constructor (parameter-less) </p>
 *
 * @author Hannes Dorfmann
 * @see TypeConverter
 * @since 1.0
 */
public interface TypeAdapter<T> {

  /**
   * This name will be used as class suffix for the generated TypeAdapter by annotation processing
   */
  String GENERATED_CLASS_SUFFIX = "$$TypeAdapter";

  /**
   * Read a java object from xml document
   *
   * @param reader The {@link XmlReader} to read the xml document
   * @param config The {@link TikXmlConfig} where you can access {@link TypeConverter} etc.
   * @return The instantiated java object of type T
   * @throws IOException
   */
  T fromXml(XmlReader reader, TikXmlConfig config) throws IOException;

  /**
   * Writes a java object as xml
   *
   * @param writer The {@link XmlWriter} to write xml
   * @param config The {@link TikXmlConfig} where you can access {@link TypeConverter} etc.
   * @param value The value to write as xml
   * @param overridingXmlElementTagName If this parameter is not null then this string should be
   * used as the xml elements tag name (instead of the default one) like {@code <someName> }. This
   * is used when you explicitly want to set a different name i.e. {@code @Element(name =
   * "someName")} Annotation instead of using the default tag name
   * @throws IOException
   */
  void toXml(XmlWriter writer, TikXmlConfig config, T value, String overridingXmlElementTagName)
      throws IOException;
}
