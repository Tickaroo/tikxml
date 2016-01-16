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
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
public class CompanyTypeAdapter extends DelegatingTypeAdapter<Company> {

  public CompanyTypeAdapter() {
    this(true);
  }

  public CompanyTypeAdapter(boolean shouldReadTextContent) {
    super(shouldReadTextContent);

    attributeBinders.put("id", new AttributeBinder<Company>() {
      @Override
      public void fromXml(XmlReader reader, TikXmlConfig config, Company value) throws IOException {
        value.id = reader.nextAttributeValueAsInt();
      }
    });

    attributeBinders.put("name", new AttributeBinder<Company>() {
      @Override
      public void fromXml(XmlReader reader, TikXmlConfig config, Company value) throws IOException {
        value.name = reader.nextAttributeValue();
      }
    });

    //
    // Child Elements
    //
    childElementBinders.put("legalForm", new ChildElementBinder<Company>() {
      @Override
      public void fromXml(XmlReader reader, TikXmlConfig config, Company value) throws IOException {
        value.legalForm = reader.nextTextContent();
      }
    });


    childElementBinders.put("founded", new ChildElementBinder<Company>() {
      @Override
      public void fromXml(XmlReader reader, TikXmlConfig config, Company value) throws IOException {
        try {
          value.founded = config.getTypeConverter(Date.class).read(reader.nextTextContent());
        } catch (Exception e) {
          throw new IOException(e);
        }
      }
    });

  }


  @Override
  protected Company newInstance() {
    return new Company();
  }

  @Override
  protected void assignTextContent(TikXmlConfig config, String textContent, Company value) {
    value.description = textContent;
  }
}
