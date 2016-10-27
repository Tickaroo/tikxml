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
 * With this annotation we can "emulate a virtual node". This allows to add some memory
 * optimizations, because we don't have to instantiate extra wrapper java objects.
 *
 * <pre>
 *   {@code
 *   <shop>
 *      <bookstore name="Lukes bookstore">
 *        <inventory>
 *          <book title="Effective Java" />
 *          <newspaper title="New York Times n. 192" />
 *        </inventory>
 *      </bookstore>
 *    </shop>
 *   }
 * </pre>
 *
 * <pre>
 *   {@code
 * @Xml
 * class Shop {
 *
 * @Path("bookstore[name]") // attributes name between '[' and ']'
 * @Attribute
 * String name
 *
 * @Path("bookstore/inventory")  //  '/' indicates child element
 * @Element
 * Book book;
 *
 * @Path("bookstore/inventory")
 * @Element
 * Newspaper newspaper;
 * }
 *   }
 * </pre>
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface Path {
  /**
   * The path. A slash ("/") is used to construct sub paths. A name in square brackets means the
   * attribute with the given name.
   */
  String value();
}
