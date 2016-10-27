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
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Class is responsible to manage and load {@link TypeAdapter}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
final class TypeAdapters {

  private final static String AUTO_VALUE_NAME_PREFIX = "\\$*AutoValue_.+";
  private final Map<Type, TypeAdapter<?>> adaptersCache = new HashMap<Type, TypeAdapter<?>>();

  TypeAdapters() {
  } // package visibility

  /**
   * Add / register a {@link TypeAdapter} for the given class
   *
   * @param clazz The class you want to register a type adapter for
   * @param adapter The {@link TypeAdapter} for the given class
   * @param <T> The generic type of the adapter
   */
  <T> void add(Type clazz, TypeAdapter<T> adapter) {
    adaptersCache.put(clazz, adapter);
  }

  /**
   * Get the a {@link TypeAdapter} for the given class
   *
   * @param type The class you want to query a TypeAdapter for
   * @param <T> The generic type of the TypeAdapter
   * @return The {@link TypeAdapter} for the given class
   * @throws IOException If no {@link TypeAdapter} has been found or could be loaded dynamically via
   * reflections
   */
  public <T> TypeAdapter<T> get(Type type) throws TypeAdapterNotFoundException {

    type = Types.canonicalize(type);

    TypeAdapter<T> adapter = (TypeAdapter<T>) adaptersCache.get(type);

    if (adapter != null) {
      return adapter;
    } else if (type instanceof Class) {
      Class clazz = (Class) type;
      // try to load TypeAdapter via reflections
      StringBuilder qualifiedTypeAdapterClassName = new StringBuilder();
      try {
        Package packageElement = clazz.getPackage();
        if (packageElement != null) {
          String packageName = packageElement.getName();
          if (packageName != null && packageName.length() > 0) {
            qualifiedTypeAdapterClassName.append(packageElement.getName());
            qualifiedTypeAdapterClassName.append('.');
          }
        }

        qualifiedTypeAdapterClassName.append(clazz.getSimpleName());
        qualifiedTypeAdapterClassName.append(TypeAdapter.GENERATED_CLASS_SUFFIX);

        try {
          Class<TypeAdapter<T>> adapterClass =
              (Class<TypeAdapter<T>>) Class.forName(qualifiedTypeAdapterClassName.toString());

          TypeAdapter<T> adapterInstance = adapterClass.newInstance();
          adaptersCache.put(clazz, adapterInstance);
          return adapterInstance;
        } catch (ClassNotFoundException e) {
          if (clazz.getSimpleName().matches(AUTO_VALUE_NAME_PREFIX)) {
            // Special case for Auto_Value generated classe we have to scan the inheritance hierarchy
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
              TypeAdapter<T> superClassAdapter = get(superClass);
              adaptersCache.put(clazz, superClassAdapter);
              return superClassAdapter;
            } else {
              // No more super class
              throw new TypeAdapterNotFoundException("No TypeAdapter for class "
                  + clazz.getCanonicalName()
                  + " found. Expected name of the type adapter is "
                  + qualifiedTypeAdapterClassName.toString(), e);
            }
          } else {
            throw new TypeAdapterNotFoundException("No TypeAdapter for class "
                + clazz.getCanonicalName()
                + " found. Expected name of the type adapter is "
                + qualifiedTypeAdapterClassName.toString(), e);
          }
        }
      } catch (InstantiationException | IllegalAccessException e) {
        throw new TypeAdapterNotFoundException("No TypeAdapter for class "
            + clazz.getCanonicalName()
            + " found. Expected name of the type adapter is "
            + qualifiedTypeAdapterClassName.toString(), e);
      }
    } else {
      throw new TypeAdapterNotFoundException(
          "No generated nor manually added TypeAdapter has been found for " + type.toString());
    }
  }
}
