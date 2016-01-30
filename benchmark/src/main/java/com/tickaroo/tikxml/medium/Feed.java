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

package com.tickaroo.tikxml.medium;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Path;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;

@Xml
public class Feed {

  @PropertyElement
  public String id;

  @PropertyElement
  public String title;

  @PropertyElement
  public String updated;

  @PropertyElement
  public String logo;

  @Path("author")
  @PropertyElement(name = "name")
  public String author;

  @Element
  public Link link;

  @PropertyElement
  public String generator;

  @Element
  public List<Entry> entries;

  public String toString() {
    return "Feed{" +
        "id='" + id + '\'' +
        ", title='" + title + '\'' +
        ", updated='" + updated + '\'' +
        ", author=" + author +
        ", logo='" + logo + '\'' +
        ", link='" + link + '\'' +
        ", generator='" + generator + '\'' +
        ", entries=" + entries +
        '}';
  }
}