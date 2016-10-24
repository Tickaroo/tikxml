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

package com.tickaroo.tikxml.reading;

import com.tickaroo.tikxml.TikXmlConfig;
import com.tickaroo.tikxml.typeadapter.TypeAdapter;
import com.tickaroo.tikxml.XmlReader;
import com.tickaroo.tikxml.XmlWriter;
import java.io.IOException;

/**
 * @author Hannes Dorfmann
 */
public class BookTypeAdapter implements TypeAdapter<Book> {


  @Override
  public Book fromXml(XmlReader reader, TikXmlConfig config) throws IOException {

    Book book = new Book();
    while (reader.hasAttribute()) {
      if (reader.nextAttributeName().equals("id")) {
        book.id = reader.nextAttributeValue();
      } else {
        reader.skipAttributeValue();
      }
    }

    while (reader.hasElement()) {
      boolean skipped = false;
      reader.beginElement();
      String elementName = reader.nextElementName();
      if (elementName.equals("author")) {
        book.author = reader.nextTextContent();
      } else if (elementName.equals("title")) {
        book.title = reader.nextTextContent();
      } else if (elementName.equals("genre")) {
        book.genre = reader.nextTextContent();
      } else if (elementName.equals("price")) {
        book.price = Double.parseDouble(reader.nextTextContent());
      } else if (elementName.equals("description")) {
        book.description = reader.nextTextContent();
      } else {
        // skip publish_date
        reader.skipRemainingElement();
        skipped = true;
      }

      if (!skipped)
        reader.endElement();
    }

    return book;

  }

  @Override
  public void toXml(XmlWriter writer, TikXmlConfig config, Book value, String overridingXmlElementTagName) throws IOException {

  }
}
