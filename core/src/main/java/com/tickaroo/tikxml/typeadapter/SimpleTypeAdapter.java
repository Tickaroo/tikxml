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
import com.tickaroo.tikxml.XmlWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simplified {@link TypeAdapter} implementation
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public abstract class SimpleTypeAdapter<T> implements TypeAdapter<T> {

  protected Map<String, AttributeBinder<T>> attributeBinders = new HashMap<>();

  /**
   * Creates a new instance of the object of the target
   *
   * @return a new instance
   */
  protected abstract T newInstance();


  @Override
  public T fromXml(XmlReader reader, TikXmlConfig config) throws IOException {

    //
    // New instance
    //
    T value = newInstance();

    //
    // Read attributes
    //
    while (reader.hasAttribute()) {

      String attributeName = reader.nextAttributeName();
      AttributeBinder<T> attributeBinder = attributeBinders.get(attributeName);

      if (attributeBinder != null) {
        attributeBinder.fromXml(reader, config, value);
      } else {
        if (config.throwsExceptionOnMissingMapping()) {
          throw new IOException("Could not map the xml attribute with the name '" + attributeName + "' to java class. Have you annotated such a field in your java class to map this xml attribute?");
        } else {
          reader.skipAttributeValue();
        }
      }

    }

    //
    // Read child elements
    //


    return value;
  }

  @Override
  public void toXml(XmlWriter writer, TikXmlConfig config, T value) throws IOException {

  }
}
