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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class is responsible to manage and load {@link TypeAdapter}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
final class TypeAdapters {

  private final Map<Class<?>, TypeAdapter<?>> adaptersCache = new HashMap<Class<?>, TypeAdapter<?>>();

  TypeAdapters() {
  } // package visibility

  /**
   * Add / register a {@link TypeAdapter} for the given class
   *
   * @param clazz The class you want to register a type adapter for
   * @param adapter The {@link TypeAdapter} for the given class
   * @param <T> The generic type of the adapter
   */
  <T> void add(Class<T> clazz, TypeAdapter<T> adapter) {
    adaptersCache.put(clazz, adapter);
  }

  /**
   * Get the a {@link TypeAdapter} for the given class
   *
   * @param clazz The class you want to query a TypeAdapter for
   * @param <T> The generic type of the TypeAdapter
   * @return The {@link TypeAdapter} for the given class
   * @throws IOException If no {@link TypeAdapter} has been found or could be loaded dynamically via
   * reflections
   */
  public <T> TypeAdapter<T> get(Class<T> clazz) throws TypeAdapterNotFoundException {
    TypeAdapter<T> adapter = (TypeAdapter<T>) adaptersCache.get(clazz);

    if (adapter != null) {
      return adapter;
    } else {
      // try to load TypeAdapter via reflections
      try {
        String qualifiedTypeAdapterClassName = clazz.getCanonicalName() + TypeAdapter.GENERATED_CLASS_SUFFIX;
        Class<TypeAdapter<T>> adapterClass = (Class<TypeAdapter<T>>) Class.forName(qualifiedTypeAdapterClassName);
        TypeAdapter<T> adapterInstance = adapterClass.newInstance();
        adaptersCache.put(clazz, adapterInstance);
        return adapterInstance;

      } catch (ClassNotFoundException e) {
        throw new TypeAdapterNotFoundException("No TypeAdapter for class " + clazz.getCanonicalName() + " found.", e);
      } catch (InstantiationException e) {
        throw new TypeAdapterNotFoundException("No TypeAdapter for class " + clazz.getCanonicalName() + " found.", e);
      } catch (IllegalAccessException e) {
        throw new TypeAdapterNotFoundException("No TypeAdapter for class " + clazz.getCanonicalName() + " found.", e);
      }
    }
  }

}
