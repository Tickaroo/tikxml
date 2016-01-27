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

package com.tickaroo.tikxml.annotationprocessing.elementlist;

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import com.tickaroo.tikxml.annotationprocessing.TestUtils;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class CatalogueTest {

  @Test
  public void simple() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    Catalogue catalogue = xml.read(TestUtils.sourceForFile("books.xml"), Catalogue.class);

    Assert.assertEquals(10, catalogue.books.size());
    for (int i = 1; i<= 10; i++){
      Book book = catalogue.books.get(i-1);
      Date date = DateConverter.format.parse("2000-09-0"+i);

      Assert.assertEquals(i, book.id);
      Assert.assertEquals("author"+i, book.author);
      Assert.assertEquals("genre"+i, book.genre);
      Assert.assertEquals(i, book.price, 0);
      Assert.assertEquals(date, book.publishDate);
      Assert.assertEquals("description"+i, book.description);
    }
  }

  @Test
  public void inlineList() throws IOException, ParseException {

    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    InlineListCatalogue catalogue = xml.read(TestUtils.sourceForFile("books_inline.xml"), InlineListCatalogue.class);

    Assert.assertEquals(10, catalogue.books.size());
    for (int i = 1; i<= 10; i++){
      Book book = catalogue.books.get(i-1);
      Date date = DateConverter.format.parse("2000-09-0"+i);

      Assert.assertEquals(i, book.id);
      Assert.assertEquals("author"+i, book.author);
      Assert.assertEquals("genre"+i, book.genre);
      Assert.assertEquals(i, book.price, 0);
      Assert.assertEquals(date, book.publishDate);
      Assert.assertEquals("description"+i, book.description);
    }

  }
}
