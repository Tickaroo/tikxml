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

/**
 * With a type converter you can specify how to convert a String value to a concrete java object and
 * vice versa. This can be used with @Attribute and @PropertyElement to read and write "primitive"
 * (not complex java objects like a class {@code Person}; use {@link TypeAdapter} for mapping xml to
 * java classes) data types from and to xml.
 *
 * <pre> Example: {@code
 *
 * public class MyDateConverter implements TypeConverter<Date> {
 *
 * private SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd"); // SimpleDateFormat is
 * not thread safe!
 *
 * @Override public Date read(String value) throws Exception{
 *  return formatter.parse(value);
 * }
 *
 * @Override public String write(Date value) throws Exception {
 *  return formatter.format(value);
 * }
 *
 * }
 * }
 *
 * </pre>
 *
 * @author Hannes Dorfmann
 * @see TypeAdapter
 * @since 1.0
 */
public interface TypeConverter<T> {

  /**
   * Read a value (as string) from xml an convert it to a java type.
   *
   * @param value The string representation. Take the String and create your java type.
   * @return The object created from string
   */
  T read(String value) throws Exception;

  /**
   * Take an object and convert it into a string representation that can be written as xml.
   *
   * @param value The object
   * @return The string representation of an object. You should consider using {@link StringBuilder}
   */
  String write(T value) throws Exception;


  /**
   * This class is just there to represent the default case for the annotations (where no {@link
   * TypeConverter} should be used).
   *
   * @author Hannes Dorfmann
   * @since 1.0
   */
  final class NoneTypeConverter implements TypeConverter<Object> {

    private NoneTypeConverter() {
    }

    @Override
    public Object read(String value) throws Exception {
      return null;
    }

    @Override
    public String write(Object value) throws Exception {
      return null;
    }
  }
}
