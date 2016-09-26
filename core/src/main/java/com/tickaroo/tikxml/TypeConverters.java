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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is internally responsible to manage {@link TypeConverter}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
final class TypeConverters {

  private final Map<Type, TypeConverter<?>> cache = new HashMap<Type, TypeConverter<?>>();

  TypeConverters() {
  } // package visibility

  /**
   * Adds an type converter for the given class
   *
   * @param clazz The class you want to register a TypeConverter for
   * @param converter The converter for this class
   * @param <T> The generics type
   * @return The typeConverters itself
   */
  <T> TypeConverters add(Type clazz, TypeConverter<T> converter) {
    cache.put(clazz, converter);
    return this;
  }

  /**
   * Get a type converter for the given class
   *
   * @param clazz The class we want to get a type converter for
   * @param <T> The type of the type converter
   * @return The TypeConverter
   */
  public <T> TypeConverter<T> get(Type clazz) throws TypeConverterNotFoundException {
    TypeConverter<T> converter = (TypeConverter<T>) cache.get(clazz);
    if (converter == null) {
      throw new TypeConverterNotFoundException("No "
          + TypeConverter.class.getSimpleName()
          + " found for type "
          + clazz.toString()
          + ". "
          +
          "You have to add one via "
          + TikXml.class.getSimpleName()
          + "."
          + TikXml.Builder.class.getSimpleName()
          + "().addTypeAdapter()");
    }
    return converter;
  }
}
