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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a certain class as XML serializable / deserializable. Classes marked with this
 * annotation can be written to xml and be read from xml.
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Xml {

  /**
   * When writing XML this value will be used if the annotated element itself is a root element.
   * Default value is using the class name.
   *
   * @return desired element name
   */
  String name() default "";

  /**
   * Indicates whether or not we should also scan the fields from super classes all the way up in
   * the inheritance three of this class.
   */
  boolean inheritance() default true;
}
