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
import com.tickaroo.tikxml.XmlReader;
import com.tickaroo.tikxml.XmlWriter;
import com.tickaroo.tikxml.typeadapter.TypeAdapter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Hannes Dorfmann
 */
public class CatalogueTypeAdapter implements TypeAdapter<Catalogue> {

  @Override
  public Catalogue fromXml(XmlReader reader, TikXmlConfig config) throws IOException {

    Catalogue catalogue = new Catalogue();
    catalogue.books = new ArrayList<>();

    while (reader.hasElement()) {
      reader.beginElement();
      if (reader.nextElementName().equals("book")) {
        catalogue.books.add((Book) config.getTypeAdapter(Book.class).fromXml(reader, config));
      }
      reader.endElement();
    }

    return catalogue;
  }

  @Override
  public void toXml(XmlWriter writer, TikXmlConfig config, Catalogue value,
      String overridingXmlElementTagName) throws IOException {

  }
}
