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

import com.tickaroo.tikxml.typeadapter.TypeAdapter;

import java.lang.reflect.Type;

/**
 * Holds the config for parsing and writing xml via {@link TikXml}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public final class TikXmlConfig {

  boolean exceptionOnUnreadXml = false;
  TypeConverters typeConverters = new TypeConverters();
  TypeAdapters typeAdapters = new TypeAdapters();
  boolean writeDefaultXmlDeclaration = true;

  TikXmlConfig() {
  }

  /**
   * Should an exception be thrown if a mapping from xml document to corresponding java class is
   * missing?
   *
   * @return true if exception should be thrown, otherwise false
   */
  public boolean exceptionOnUnreadXml() {
    return exceptionOnUnreadXml;
  }

  /**
   * Should the default xml declaration be written
   * {@code <?xml version="1.0" encoding="UTF-8"?>}
   *
   * @return true if should be written, otherwise false
   */
  public boolean writeDefaultXmlDeclaration() {
    return writeDefaultXmlDeclaration;
  }

  /**
   * Query a {@link TypeConverter} for a given class
   *
   * @param clazz The class you want a type converter for
   * @param <T> The type of the TypeConverter
   * @return The TypeConverter or throws an Exception
   * @throws TypeConverterNotFoundException Thrown if no TypeConverter has been found for the given
   * class
   */
  public <T> TypeConverter<T> getTypeConverter(Class<T> clazz)
      throws TypeConverterNotFoundException {
    return typeConverters.get(clazz);
  }

  /**
   * Get the {@link TypeAdapter} for a given class
   *
   * @param clazz The class you want a {@link TypeAdapter}for
   * @param <T> The type of the TypeAdapter
   * @return The {@link TypeAdapter} for the given type
   * @throws TypeAdapterNotFoundException Thrown if no {@link TypeAdapter} has been found for the
   * given class
   */
  public <T> TypeAdapter<T> getTypeAdapter(Type clazz) throws TypeAdapterNotFoundException {
    return typeAdapters.get(clazz);
  }
}
