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

package com.tickaroo.tikxml.annotationprocessing.element;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Server {
  @Attribute String name;
  @Element ServerConfig config;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Server)) return false;

    Server server = (Server) o;

    if (name != null ? !name.equals(server.name) : server.name != null) return false;
    return config != null ? config.equals(server.config) : server.config == null;
  }

  @Override public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (config != null ? config.hashCode() : 0);
    return result;
  }
}
