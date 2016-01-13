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
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link ChildElementBinder} who can also have xml attributes. This class used with {@link
 * DelegatingTypeAdapter}
 *
 * @author Hannes Dorfmann
 * @see ChildElementBinder
 * @see DelegatingTypeAdapter
 * @since 1.0
 */
public class ChildElementWithAttributesBinder<T> implements ChildElementBinder<T> {

  protected Map<String, AttributeBinder<T>> attributeBinders = new HashMap<>();

  @Override
  public void fromXml(XmlReader reader, TikXmlConfig config, T value) throws IOException {

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

  }
}
