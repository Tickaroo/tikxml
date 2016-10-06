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
import java.util.Map;

/**
 * A {@link ChildElementBinder} who can also have xml attributes and nested xml elements. This class
 * used with {@link DelegatingTypeAdapter}
 *
 * @author Hannes Dorfmann
 * @see ChildElementBinder
 * @see DelegatingTypeAdapter
 * @since 1.0
 */
@Deprecated
public abstract class NestedChildElementBinder<T> implements ChildElementBinder<T> {

  public Map<String, AttributeBinder<T>> attributeBinders = null;
  public Map<String, ChildElementBinder<T>> childElementBinders = null;

  // TODO Maybe use a Pool of StringBuilders?

  //
  // Text content
  //
  private String textContent = null;
  private StringBuilder textContentBuilder = null;
  private final boolean shouldReadTextContent;

  /**
   * For memory optimization you can specify here if attributes are read and child elements are
   * read
   */
  public NestedChildElementBinder(boolean shouldReadTextContent) {
    this.shouldReadTextContent = shouldReadTextContent;
  }

  /**
   * Override this method and call super.fromXml() to read attributes
   *
   * @param reader The {@link XmlReader}
   * @throws IOException
   */
  @Override
  public void fromXml(XmlReader reader, TikXmlConfig config, T value) throws IOException {

    //
    // Read attributes
    //
    if (reader.hasAttribute()) {

      if (attributeBinders != null) {
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
                  + "to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().");
            } else {
              reader.skipAttributeValue();
            }
          }
        }
      } else {
        // Skip the attributes if no attributes binder is registered
        while (reader.hasAttribute()) {
          reader.skipAttribute();
        }
      }
    }

    //
    // Read child elements
    //
    while (true) {
      if (childElementBinders != null && reader.hasElement()) {
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

    //
    // Assign text content if any
    //
    if (shouldReadTextContent) {
      if (textContentBuilder != null && textContentBuilder.length() > 0) {
        assignTextContent(config, textContentBuilder.toString(), value);
        textContentBuilder.setLength(0);
      } else if (textContent != null) {
        assignTextContent(config, textContent, value);
        textContent = null;
      }
    }
  }

  /**
   * Override this method if your chile element has a text content
   *
   * @param config the config
   * @param textContent the textvalue as string
   * @param value the value where we have to assign the text content value
   */
  protected void assignTextContent(TikXmlConfig config, String textContent, T value) {

  }
}
