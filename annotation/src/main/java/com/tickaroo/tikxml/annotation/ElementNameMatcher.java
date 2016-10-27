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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to resolve polymorphism by scanning for a certain xml tag
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementNameMatcher {

  /**
   * The name of the xml element. Whenever an element with this xml element (tag name) occurres it
   * will be parsed into the class defined in {@link #type()}. This overrides the default name of
   * the {@link Xml#name()} annotated class.
   */
  String name() default "";

  /**
   * The type to which we want to parse into
   */
  Class<?> type();

  /**
   * Should at compile time be checked if the annotated type ({@link #type()} is a valid TikXml annotated
   * class (i.e. no abstract class, interface, etc.)
   */
  boolean compileTimeChecks() default true;
}
