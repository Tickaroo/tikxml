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

package com.tickaroo.tikxml.annotationprocessing.elementlist.constructor;

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import com.tickaroo.tikxml.TestUtils;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class CatalogueConstructorTest {

  @Test
  public void simple() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    CatalogueConstructor catalogue = xml.read(TestUtils.sourceForFile("books.xml"), CatalogueConstructor.class);

    Assert.assertEquals(10, catalogue.getBooks().size());
    for (int i = 1; i<= 10; i++){
      BookConstructor book = catalogue.getBooks().get(i-1);
      Date date = DateConverter.format.parse("2000-09-0"+i);

      Assert.assertEquals(i, book.getId());
      Assert.assertEquals("author"+i, book.getAuthor());
      Assert.assertEquals("genre"+i, book.getGenre());
      Assert.assertEquals(i, book.getPrice(), 0);
      Assert.assertEquals(date, book.getPublishDate());
      Assert.assertEquals("description"+i, book.getDescription());
    }
  }


  @Test
  public void simpleWithAttributeInPath() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    CatalogueConstructor catalogue = xml.read(TestUtils.sourceForFile("books_with_attribute_with_path.xml"), CatalogueConstructor.class);

    Assert.assertEquals(10, catalogue.getBooks().size());
    for (int i = 1; i<= 10; i++){
      BookConstructor book = catalogue.getBooks().get(i-1);
      Date date = DateConverter.format.parse("2000-09-0"+i);

      Assert.assertEquals(i, book.getId());
      Assert.assertEquals("author"+i, book.getAuthor());
      Assert.assertEquals("genre"+i, book.getGenre());
      Assert.assertEquals(i, book.getPrice(), 0);
      Assert.assertEquals(date, book.getPublishDate());
      Assert.assertEquals("description"+i, book.getDescription());
    }
  }

  @Test
  public void inlineList() throws IOException, ParseException {

    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    InlineListCatalogueConstructor catalogue = xml.read(TestUtils.sourceForFile("books_inline.xml"), InlineListCatalogueConstructor.class);

    Assert.assertEquals(10, catalogue.getBooks().size());
    for (int i = 1; i<= 10; i++){
      BookConstructor book = catalogue.getBooks().get(i-1);
      Date date = DateConverter.format.parse("2000-09-0"+i);

      Assert.assertEquals(i, book.getId());
      Assert.assertEquals("author"+i, book.getAuthor());
      Assert.assertEquals("genre"+i, book.getGenre());
      Assert.assertEquals(i, book.getPrice(), 0);
      Assert.assertEquals(date, book.getPublishDate());
      Assert.assertEquals("description"+i, book.getDescription());
    }

  }
}
