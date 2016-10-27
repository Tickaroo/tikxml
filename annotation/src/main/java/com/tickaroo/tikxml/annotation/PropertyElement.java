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

package com.tickaroo.tikxml.annotation;

import com.tickaroo.tikxml.TypeConverter;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A PropertyElement is a special {@link Element} that maps a xml element content to one java
 * primitive type (like int, double, string, etc.)
 *
 * <pre>
 *   {@code
 * <book id="123">
 * <title>Effective Java</title>
 * <author>Joshua Bloch</author>
 * <publish_date>2015-11-25</publish_date>
 * </book>
 * }
 * </pre>
 *
 * <pre>
 *   {@code
 * @Xml
 * public class Book {
 *
 * @Attribute
 * String id;
 *
 * @PropertyElement
 * String title;
 *
 * @PropertyElement
 * String author;
 *
 * @PropertyElement(name = "publish_date", converter = MyDateConverter.class)
 * Date published;
 * }
 *   }
 * </pre>
 *
 * <p> <b>Property Elements are not allowed to be empty!</b> i.e. parsing the same example as
 * above:
 * <pre>
 *     {@code
 * <book id="123">
 * <title></title>
 * <author>Joshua Bloch</author>
 * <publish_date>2015-11-25</publish_date>
 * </book>
 *     }
 *   </pre>
 *
 * but with an empty tag {@code <title></title> } the empty string will be set. In general, default
 * values will be set for empty tags. Empty string for Strings, false for booleans, 0 for numbers
 * (like int, double, long ...) </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface PropertyElement {

  /**
   * Specify the name of the xml element
   *
   * @return The specified name. Default value is the name of the annotated java class field.
   */
  String name() default "";

  /**
   * Specify which {@link TypeConverter} you want to use to serialize / deserialize the given xml
   * element content.
   *
   * @return The TypeConverter class to use
   */
  Class<? extends TypeConverter> converter() default TypeConverter.NoneTypeConverter.class;

  /**
   * Write this ElementProperties value as CDATA when generating XML?
   *
   * @return true if CDATA, otherwise false
   */
  boolean writeAsCData() default false;
}
