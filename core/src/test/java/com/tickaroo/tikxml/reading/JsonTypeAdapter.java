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

public class JsonTypeAdapter implements TypeAdapter<JsonElement> {


  @Override
  public JsonElement fromXml(XmlReader reader, TikXmlConfig config) throws IOException {
    JsonElement json = new JsonElement();
    while (reader.hasElement()) {
      boolean skipped = false;
      reader.beginElement();
      String elementName = reader.nextElementName();
      if (elementName.equals("json")) {
        json.json = reader.nextTextContent();
      } else {
        // skip publish_date
        reader.skipRemainingElement();
        skipped = true;
      }

      if (!skipped)
        reader.endElement();
    }

    return json;

  }

  @Override
  public void toXml(XmlWriter writer, TikXmlConfig config, JsonElement value) throws IOException {

  }
}
