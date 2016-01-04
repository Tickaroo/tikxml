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

package com.tickaroo.tikxml;

/**
 * As the name already suggests, a type adapter is responsible to convert a xml element to a java
 * object and vice versa.
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public interface TypeAdapter<T> {

  /**
   * Read a java object from xml document
   * @param reader The {@link XmlReader} to read the xml document
   * @return The instantiated java object of type T
   */
  public T fromXml(XmlReader reader);

  /**
   *
   * @param writer
   * @param value
   */
  public void toXml(XmlWriter writer, T value);
}
