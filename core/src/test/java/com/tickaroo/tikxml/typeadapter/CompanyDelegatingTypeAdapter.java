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

/**
 * @author Hannes Dorfmann
 */
public class CompanyDelegatingTypeAdapter extends DelegatingTypeAdapter<Company> {


  public CompanyDelegatingTypeAdapter() {

    attributeBinders.put("id", new AttributeBinder<Company>() {
      @Override
      public void fromXml(XmlReader reader, TikXmlConfig config, Company value) throws IOException {
        value.id = reader.nextAttributeValueAsInt();
      }

      @Override
      public void toXml(XmlWriter writer, TikXmlConfig config, Company value) throws IOException {

      }
    });

    attributeBinders.put("name", new AttributeBinder<Company>() {
      @Override
      public void fromXml(XmlReader reader, TikXmlConfig config, Company value) throws IOException {
        value.name = reader.nextAttributeValue();
      }

      @Override
      public void toXml(XmlWriter writer, TikXmlConfig config, Company value) throws IOException {

      }
    });

  }


  @Override
  protected Company newInstance() {
    return new Company();
  }


}
