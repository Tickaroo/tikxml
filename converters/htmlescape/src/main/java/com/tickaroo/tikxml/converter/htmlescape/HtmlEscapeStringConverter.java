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

package com.tickaroo.tikxml.converter.htmlescape;

import com.tickaroo.tikxml.TypeConverter;

/**
 * A String TypeConverter that escapes and unescapes HTML characters directly from string. This one
 * uses apache 3 StringEscapeUtils. This converter is thread safe
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class HtmlEscapeStringConverter implements TypeConverter<String> {

  public String read(String s) throws Exception {
    return StringEscapeUtils.unescapeHtml4(s);
  }

  public String write(String s) throws Exception {
    return StringEscapeUtils.escapeHtml4(s);
  }
}
