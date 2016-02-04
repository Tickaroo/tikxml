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
import com.tickaroo.tikxml.XmlReader;
import java.io.IOException;

/**
 * This binder is used with {@link DelegatingTypeAdapter} to delegate the work of reading and writing an
 * xml attribute.
 *
 * @param <T> the type of the parents object
 * @author Hannes Dorfmann
 * @since 1.0
 */
public interface AttributeBinder<T> {

  /**
   * Reads an attribute value from xml and assigns it to the objects value. A trivial implementation
   * to read an xml attribute as integer may look like this:
   * <pre>
   *   {@code
   *    class AgeAttributeBinder extends AttributeBinder<Person> {
   *
   *      public fromXml(XmlReader reader, TikXmlConfig config, Person value){
   *        value.age = reader.nextAttributeValueAsInt();
   *      }
   *    }
   *   }
   * </pre>
   *
   * @param reader The {@link XmlReader}
   * @throws IOException
   */
  void fromXml(XmlReader reader, TikXmlConfig config, T value) throws IOException;

}
