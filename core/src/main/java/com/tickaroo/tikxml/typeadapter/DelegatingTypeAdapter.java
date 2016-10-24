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
@Deprecated
public abstract class DelegatingTypeAdapter<T> implements TypeAdapter<T> {

  protected Map<String, AttributeBinder<T>> attributeBinders = new HashMap<>();
  protected Map<String, ChildElementBinder<T>> childElementBinders = new HashMap<>();

  //
  // Text content
  //
  private String textContent = null;
  private StringBuilder textContentBuilder = null;
  private final boolean shouldReadTextContent;

  /**
   * Reading text content of this root element may requires some extra object allocation. If you
   * know that you wont read text content of this root element than set this to false to allow some
   * optimizations.
   */
  public DelegatingTypeAdapter(boolean shouldReadTextContent) {
    this.shouldReadTextContent = shouldReadTextContent;
  }

  /**
   * Creates a new instance of the object of the target
   *
   * @return a new instance
   */
  protected abstract T newInstance();

  /**
   * Assign the text content to the given value. Override this in sub class if your typeadapter has
   * text content
   */
  protected void assignTextContent(TikXmlConfig config, String textContent, T value) {

  }

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
        if (config.exceptionOnUnreadXml()) {
          throw new IOException("Could not map the xml attribute with the name '"
              + attributeName
              + "' at path "
              + reader.getPath()
              + " to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().");
        } else {
          reader.skipAttributeValue();
        }
      }
    }

    //
    // Read child elements
    //
    while (true) {
      if (reader.hasElement()) {
        //
        // Read element
        //
        reader.beginElement();

        String elementName = reader.nextElementName();
        ChildElementBinder<T> childElementBinder = childElementBinders.get(elementName);
        if (childElementBinder != null) {
          childElementBinder.fromXml(reader, config, value);
          reader.endElement();
        } else {
          if (config.exceptionOnUnreadXml()) {
            throw new IOException("Could not map the xml element with the name '"
                + elementName
                + "' at path "
                + reader.getPath()
                + " to java class. Have you annotated such a field in your java class to map this xml element? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().");
          } else {
            reader.skipRemainingElement(); // includes reader.endElement()
          }
        }
      } else if (reader.hasTextContent()) {
        //
        // Read text content
        //

        if (shouldReadTextContent) {
          if (textContent == null) {
            textContent = reader.nextTextContent();
          } else {
            // optimization: If textContent is split in parts (xml elements siting in between) than use StringBuilder
            if (textContentBuilder == null) {
              textContentBuilder = new StringBuilder(textContent);
            }
            textContentBuilder.append(reader.nextTextContent());
          }
        } else {
          if (config.exceptionOnUnreadXml()) {
            throw new IOException("Could not map the xml element's text content at path  at path "
                + reader.getPath()
                + " to java class. Have you annotated such a field in your java class to map the xml element's text content? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().");
          } else {
            reader.skipTextContent();
          }
        }
      } else {
        break;
      }
    }

    // Assign the text content
    if (shouldReadTextContent) {
      if (textContentBuilder != null && textContentBuilder.length() > 0) {
        assignTextContent(config, textContentBuilder.toString(), value);
        textContentBuilder.setLength(0);
      } else if (textContent != null) {
        assignTextContent(config, textContent, value);
        textContent = null;
      }
    }

    return value;
  }

  @Override
  public void toXml(XmlWriter writer, TikXmlConfig config, T value, String overridingXmlElementTagName) throws IOException {

  }
}
