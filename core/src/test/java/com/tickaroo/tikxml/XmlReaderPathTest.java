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

package com.tickaroo.tikxml;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

import static com.tickaroo.tikxml.TestUtils.readerFrom;

/**
 * @author Hannes Dorfmann
 */
public class XmlReaderPathTest {

  @Test
  public void test() throws IOException {
    XmlReader reader = readerFrom("<element a='123' b=\"qwe\">value<bar a='123' /></element>");

    try {
      reader.beginElement();
      reader.nextElementName();
      Assert.assertEquals("/element", reader.getPath());

      reader.nextAttributeName();
      Assert.assertEquals("/element[@a]", reader.getPath());
      reader.nextAttributeValue();
      Assert.assertEquals("/element", reader.getPath());

      reader.nextAttributeName();
      Assert.assertEquals("/element[@b]", reader.getPath());

      reader.nextAttributeValue();
      Assert.assertEquals("/element", reader.getPath());

      reader.nextTextContent();
      Assert.assertEquals("/element/text()", reader.getPath());

      reader.beginElement();
      reader.nextElementName();
      Assert.assertEquals("/element/bar", reader.getPath());
      reader.nextAttributeName();
      Assert.assertEquals("/element/bar[@a]", reader.getPath());
      reader.nextAttributeValue();
      Assert.assertEquals("/element/bar", reader.getPath());
      reader.endElement();

      Assert.assertEquals("/element/text()", reader.getPath());
      reader.endElement();

      Assert.assertEquals("/", reader.getPath());
    } finally {
      reader.close();
    }
  }

}
