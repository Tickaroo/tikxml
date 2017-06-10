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

package com.tickaroo.tikxml.converter;

import com.tickaroo.tikxml.converter.htmlescape.HtmlEscapeStringConverter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class HtmlEscapeStringConverterTest {

  @Test
  public void testEscaping() throws Exception {

    String xml =
        "&quot;&amp;&lt;&gt;&auml;&Agrave;&Ouml;&szlig;&uuml;&laquo;&raquo;&bdquo;&ldquo;&rdquo;";
    String expected = "\"&<>äÀÖßü«»„“”";

    HtmlEscapeStringConverter converter = new HtmlEscapeStringConverter();
    String converted = converter.read(xml);

    Assert.assertEquals(expected, converted);

    Assert.assertEquals(xml, converter.write(expected));
  }

  @Test
  public void test() throws Exception {
    String toDecode =
        "Der Brand ist am Donnerstag kurz nach 21 Uhr auf dem gro&szlig;en Bauernhof mit Geb&auml;udekomplex";
    String expected =
        "Der Brand ist am Donnerstag kurz nach 21 Uhr auf dem großen Bauernhof mit Gebäudekomplex";

    HtmlEscapeStringConverter converter = new HtmlEscapeStringConverter();
    String converted = converter.read(toDecode);

    Assert.assertEquals(expected, converted);
  }
}
