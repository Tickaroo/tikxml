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
 * This annotation is used to map xml element attributes to java fields. {@code <book id="123" /> }
 * maps to {@code
 *
 * @author Hannes Dorfmann
 * @Xml class Book {
 * @Attribute int id; } }
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface Attribute {

  /**
   * Specify the name of the attribute
   *
   * @return The specified name. Default value is the name of the annotated java class field.
   */
  String name() default "";

  /**
   * Specify which {@link TypeConverter} you want to use to serialize / deserialize the given xml
   * attribute.
   *
   * @return The TypeConverter class to use
   */
  Class<? extends TypeConverter> converter() default TypeConverter.NoneTypeConverter.class;
}
