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
 * This annotation is used to read the text content of an xml element.
 *
 * In XML the content of an xml element can be a mix of text and child elements:
 * <pre>
 * {@code
 * <book>
 *  <title>Effective Java</title>
 *    <author>
 *      <name>Joshua Bloch</name>
 *    </author>
 *  This book talks about tips and tricks and best practices for java developers
 * </book>
 * }
 * </pre>
 *
 * To read this "This book talks about tips and tricks and best practices for java developers" we
 * need this extra annotation.
 *
 * <pre>
 *   {@code
 *     @Xml
 * class Book {
 *
 * @PropertyElement
 * String title;
 *
 * @Element
 * Author author;
 *
 * @TextContent
 * String description; // Contains the text "This book talks about ..."
 * }
 *   }
 * </pre>
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface TextContent {

  /**
   * Write this ElementProperties value as CDATA when generating XML?
   *
   * @return true if CDATA, otherwise false
   */
  boolean writeAsCData() default false;
}
