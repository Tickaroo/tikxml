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
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package com.tickaroo.tikxml.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used in combination with {@link Element} annotation to indicate that a certain
 * List of elements should be inline (without an extra surrounding element).
 *
 * <pre>
 * {@code
 * @Xml
 * class Catalogue {
 *
 * @Element
 * List<Book> books;
 *
 * }
 * }
 * </pre>
 *
 * which will read and write the following xml:
 *
 * <pre>
 *   {@code
 *   <catalog>
 *     <books>
 *       <book id="1">...</book>
 *       <book id="2">...</book>
 *       <book id="3">...</book>
 *      </books>
 *   </catalog>
 * }
 * </pre>
 *
 * So there is this extra surrounding {@code <books>} tag arround the list of elements. With the
 * {@code @InlineList} annotation we can remove this additional tag to read and write the following
 * xml:
 *
 * <pre>
 *   {@code
 *   <catalog>
 *       <book id="1">...</book>
 *       <book id="2">...</book>
 *       <book id="3">...</book>
 *   </catalog>
 * }
 * </pre>
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InlineList {
}
