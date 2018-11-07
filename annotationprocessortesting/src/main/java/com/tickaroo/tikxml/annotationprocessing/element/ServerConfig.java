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
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class ServerConfig {

  @Attribute boolean enabled;
  @PropertyElement String ip;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ServerConfig)) return false;

    ServerConfig that = (ServerConfig) o;

    if (enabled != that.enabled) return false;
    return ip != null ? ip.equals(that.ip) : that.ip == null;
  }

  @Override public int hashCode() {
    int result = (enabled ? 1 : 0);
    result = 31 * result + (ip != null ? ip.hashCode() : 0);
    return result;
  }
}
