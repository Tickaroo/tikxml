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

/**
 * Specify how the XML parser / writer should scan for the xml to java class mapping
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public enum ScanMode {

  /**
   * Take the mode the global default scan mode (can be set for each TikXml instance). The default
   * value is {@link #COMMON_CASE}.
   */
  DEFAULT,

  /**
   * When using this mode all primitive data types are mapped to xml attributes (is equal to
   * annotating class fields with {@link Attribute}). All non primitive types (in other words
   * objects) are mapped to child objects (is equal to annotating class fields with {@link
   * Element}).
   *
   * <p> Example: {@code <book id="123" title="Effective java"> <author>...</author> </book> }
   * {@code
   *
   * @Xml(mode = ScanMode.COMMON_CASE) class Book { int id;          // Doesn't need an @Attribute
   * annotation String title;    // Doesn't need an @Attribute annotation Author author;   //
   * Doesn't need an @Element annotation } } </p>
   *
   * To ignore fields use {@link IgnoreXml}
   */
  COMMON_CASE,

  /**
   * Only respects fields with annotations.
   */
  ANNOTATIONS_ONLY
}
