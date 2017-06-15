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

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class BookConstructor {

  private int id;
  private String author;
  private String title;
  private String genre;
  private Date publishDate;
  private double price;
  private String description;

  public BookConstructor(@Attribute int id, @PropertyElement String author,
      @PropertyElement String title, @PropertyElement String genre,
      @PropertyElement(name = "publish_date", converter = DateConverter.class) Date publishDate,
      @PropertyElement double price, @PropertyElement String description) {
    this.id = id;
    this.author = author;
    this.title = title;
    this.genre = genre;
    this.publishDate = publishDate;
    this.price = price;
    this.description = description;
  }

  public int getId() {
    return id;
  }

  public String getAuthor() {
    return author;
  }

  public String getTitle() {
    return title;
  }

  public String getGenre() {
    return genre;
  }

  public Date getPublishDate() {
    return publishDate;
  }

  public double getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }
}
