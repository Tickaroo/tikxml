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
 * With this annotation you indicate that a xml child element maps to a certain java class.
 *
 * <pre>
 *   {@code
 *   <book id="123">
 *      <title>Effective Java</title>
 *      <author>     <!-- child element -->
 *         <firstname>Joshua</firstname>
 *          <lastname>Bloch</lastname>
 *      </author>
 *   </book>
 *   }
 * </pre>
 *
 * <pre>
 *   {@code
 *
 *   @Xml
 *   public class Book {
 *
 *   @Attribute
 *   String id;
 *
 *   @PropertyElement
 *   String title;
 *
 *   @Element(name = "author") // name is optional, field name will be used as default value
 *   Author author;
 *   }
 *   }
 * </pre>
 *
 * @author Hannes Dorfmann
 * @see PropertyElement
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface Element {

  /**
   * The name of the xml element
   *
   * @return The name of the xml element. Default value is the name of the annotated java field.
   */
  String name() default "";

  /**
   * Define here how to resolve polymorphism
   *
   * @return A list of {@link ElementNameMatcher} that will be used to resolve polymorphism and
   * inheritance.
   */
  ElementNameMatcher[] typesByElement() default {};

  /**
   * Should at compile time be checked if the annotated element's type is a valid TikXml annotated
   * class (i.e. no abstract class, interface, etc.)
   */
  boolean compileTimeChecks() default true;
}
