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

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Book {

  @Attribute int id;
  @PropertyElement String author;
  @PropertyElement String title;
  @PropertyElement String genre;
  @PropertyElement(name = "publish_date", converter = DateConverter.class) Date publishDate;
  @PropertyElement double price;
  @PropertyElement String description;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Book)) return false;

    Book book = (Book) o;

    if (id != book.id) return false;
    if (Double.compare(book.price, price) != 0) return false;
    if (author != null ? !author.equals(book.author) : book.author != null) return false;
    if (title != null ? !title.equals(book.title) : book.title != null) return false;
    if (genre != null ? !genre.equals(book.genre) : book.genre != null) return false;
    if (publishDate != null ? !publishDate.equals(book.publishDate) : book.publishDate != null) {
      return false;
    }
    return description != null ? description.equals(book.description) : book.description == null;
  }

  @Override public int hashCode() {
    int result;
    long temp;
    result = id;
    result = 31 * result + (author != null ? author.hashCode() : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (genre != null ? genre.hashCode() : 0);
    result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
    temp = Double.doubleToLongBits(price);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (description != null ? description.hashCode() : 0);
    return result;
  }
}
