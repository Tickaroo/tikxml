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

package com.tickaroo.tikxml.annotationprocessing.elementlist.autovalue;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class CatalogueTest {

  @Test
  public void simple() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    Catalogue catalogue = xml.read(TestUtils.sourceForFile("books.xml"), Catalogue.class);

    Assert.assertEquals(10, catalogue.books().size());
    for (int i = 1; i <= 10; i++) {
      Book book = catalogue.books().get(i - 1);
      Date date = DateConverter.format.parse("2000-09-0" + i);

      Assert.assertEquals(i, book.id());
      Assert.assertEquals("author" + i, book.author());
      Assert.assertEquals("genre" + i, book.genre());
      Assert.assertEquals(i, book.price(), 0);
      Assert.assertEquals(date, book.publishDate());
      Assert.assertEquals("description" + i, book.description());
    }

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, catalogue);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><catalogue><books><book id=\"1\"><author>author1</author><title>title1</title><genre>genre1</genre><publish_date>2000-09-01</publish_date><price>1.0</price><description>description1</description></book><book id=\"2\"><author>author2</author><title>title2</title><genre>genre2</genre><publish_date>2000-09-02</publish_date><price>2.0</price><description>description2</description></book><book id=\"3\"><author>author3</author><title>title3</title><genre>genre3</genre><publish_date>2000-09-03</publish_date><price>3.0</price><description>description3</description></book><book id=\"4\"><author>author4</author><title>title4</title><genre>genre4</genre><publish_date>2000-09-04</publish_date><price>4.0</price><description>description4</description></book><book id=\"5\"><author>author5</author><title>title5</title><genre>genre5</genre><publish_date>2000-09-05</publish_date><price>5.0</price><description>description5</description></book><book id=\"6\"><author>author6</author><title>title6</title><genre>genre6</genre><publish_date>2000-09-06</publish_date><price>6.0</price><description>description6</description></book><book id=\"7\"><author>author7</author><title>title7</title><genre>genre7</genre><publish_date>2000-09-07</publish_date><price>7.0</price><description>description7</description></book><book id=\"8\"><author>author8</author><title>title8</title><genre>genre8</genre><publish_date>2000-09-08</publish_date><price>8.0</price><description>description8</description></book><book id=\"9\"><author>author9</author><title>title9</title><genre>genre9</genre><publish_date>2000-09-09</publish_date><price>9.0</price><description>description9</description></book><book id=\"10\"><author>author10</author><title>title10</title><genre>genre10</genre><publish_date>2000-09-10</publish_date><price>10.0</price><description>description10</description></book></books></catalogue>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    Catalogue catalogue2 = xml.read(TestUtils.sourceFrom(xmlStr), Catalogue.class);
    Assert.assertEquals(catalogue, catalogue2);
  }

  @Test
  public void simpleWithAttributeInPath() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();
    Catalogue catalogue =
        xml.read(TestUtils.sourceForFile("books_with_attribute_with_path.xml"), Catalogue.class);

    Assert.assertEquals(10, catalogue.books().size());
    for (int i = 1; i <= 10; i++) {
      Book book = catalogue.books().get(i - 1);
      Date date = DateConverter.format.parse("2000-09-0" + i);

      Assert.assertEquals(i, book.id());
      Assert.assertEquals("author" + i, book.author());
      Assert.assertEquals("genre" + i, book.genre());
      Assert.assertEquals(i, book.price(), 0);
      Assert.assertEquals(date, book.publishDate());
      Assert.assertEquals("description" + i, book.description());
    }

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, catalogue);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><catalogue><books><book id=\"1\"><author>author1</author><title>title1</title><genre>genre1</genre><publish_date>2000-09-01</publish_date><price>1.0</price><description>description1</description></book><book id=\"2\"><author>author2</author><title>title2</title><genre>genre2</genre><publish_date>2000-09-02</publish_date><price>2.0</price><description>description2</description></book><book id=\"3\"><author>author3</author><title>title3</title><genre>genre3</genre><publish_date>2000-09-03</publish_date><price>3.0</price><description>description3</description></book><book id=\"4\"><author>author4</author><title>title4</title><genre>genre4</genre><publish_date>2000-09-04</publish_date><price>4.0</price><description>description4</description></book><book id=\"5\"><author>author5</author><title>title5</title><genre>genre5</genre><publish_date>2000-09-05</publish_date><price>5.0</price><description>description5</description></book><book id=\"6\"><author>author6</author><title>title6</title><genre>genre6</genre><publish_date>2000-09-06</publish_date><price>6.0</price><description>description6</description></book><book id=\"7\"><author>author7</author><title>title7</title><genre>genre7</genre><publish_date>2000-09-07</publish_date><price>7.0</price><description>description7</description></book><book id=\"8\"><author>author8</author><title>title8</title><genre>genre8</genre><publish_date>2000-09-08</publish_date><price>8.0</price><description>description8</description></book><book id=\"9\"><author>author9</author><title>title9</title><genre>genre9</genre><publish_date>2000-09-09</publish_date><price>9.0</price><description>description9</description></book><book id=\"10\"><author>author10</author><title>title10</title><genre>genre10</genre><publish_date>2000-09-10</publish_date><price>10.0</price><description>description10</description></book></books></catalogue>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    Catalogue catalogue2 = xml.read(TestUtils.sourceFrom(xmlStr), Catalogue.class);
    Assert.assertEquals(catalogue, catalogue2);
  }

  @Test
  public void inlineList() throws IOException, ParseException {

    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).build();

    InlineListCatalogue catalogue =
        xml.read(TestUtils.sourceForFile("books_inline.xml"), InlineListCatalogue.class);

    Assert.assertEquals(10, catalogue.books().size());
    for (int i = 1; i <= 10; i++) {
      Book book = catalogue.books().get(i - 1);
      Date date = DateConverter.format.parse("2000-09-0" + i);

      Assert.assertEquals(i, book.id());
      Assert.assertEquals("author" + i, book.author());
      Assert.assertEquals("genre" + i, book.genre());
      Assert.assertEquals(i, book.price(), 0);
      Assert.assertEquals(date, book.publishDate());
      Assert.assertEquals("description" + i, book.description());
    }

    // Write XML
    // Writing tests
    Buffer buffer = new Buffer();
    xml.write(buffer, catalogue);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><catalogue><book id=\"1\"><author>author1</author><title>title1</title><genre>genre1</genre><publish_date>2000-09-01</publish_date><price>1.0</price><description>description1</description></book><book id=\"2\"><author>author2</author><title>title2</title><genre>genre2</genre><publish_date>2000-09-02</publish_date><price>2.0</price><description>description2</description></book><book id=\"3\"><author>author3</author><title>title3</title><genre>genre3</genre><publish_date>2000-09-03</publish_date><price>3.0</price><description>description3</description></book><book id=\"4\"><author>author4</author><title>title4</title><genre>genre4</genre><publish_date>2000-09-04</publish_date><price>4.0</price><description>description4</description></book><book id=\"5\"><author>author5</author><title>title5</title><genre>genre5</genre><publish_date>2000-09-05</publish_date><price>5.0</price><description>description5</description></book><book id=\"6\"><author>author6</author><title>title6</title><genre>genre6</genre><publish_date>2000-09-06</publish_date><price>6.0</price><description>description6</description></book><book id=\"7\"><author>author7</author><title>title7</title><genre>genre7</genre><publish_date>2000-09-07</publish_date><price>7.0</price><description>description7</description></book><book id=\"8\"><author>author8</author><title>title8</title><genre>genre8</genre><publish_date>2000-09-08</publish_date><price>8.0</price><description>description8</description></book><book id=\"9\"><author>author9</author><title>title9</title><genre>genre9</genre><publish_date>2000-09-09</publish_date><price>9.0</price><description>description9</description></book><book id=\"10\"><author>author10</author><title>title10</title><genre>genre10</genre><publish_date>2000-09-10</publish_date><price>10.0</price><description>description10</description></book></catalogue>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    InlineListCatalogue catalogue2 =
        xml.read(TestUtils.sourceFrom(xmlStr), InlineListCatalogue.class);
    Assert.assertEquals(catalogue, catalogue2);
  }
}
