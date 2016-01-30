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
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;

@Xml
public class Entry {
  @PropertyElement
  public String id;
  @PropertyElement
  public String title;
  @PropertyElement
  public String summary;
  @PropertyElement
  public String updated;

  @Element
  public List<Link> links;

  @Override
  public String toString() {
    return "Entry{" +
        "id='" + id + '\'' +
        ", title='" + title + '\'' +
        ", summary='" + summary + '\'' +
        ", updated='" + updated + '\'' +
        ", links=" + links +
        '}';
  }
}