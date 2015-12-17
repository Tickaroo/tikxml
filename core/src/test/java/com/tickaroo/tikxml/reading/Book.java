package com.tickaroo.tikxml.reading;

/**
 * @author Hannes Dorfmann
 */
// @Xml
public class Book {

  // @Attribute (converter string -> object)
  String id;

  // @PropertyElement(name = xml-node-name)
  String author;
  String title;
  String genre;
  double price;
  String description;
}
