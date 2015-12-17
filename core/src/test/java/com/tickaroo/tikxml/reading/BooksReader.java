package com.tickaroo.tikxml.reading;

import com.tickaroo.tikxml.XmlReader;
import java.io.IOException;

/**
 * @author Hannes Dorfmann
 */
public class BooksReader implements XmlParser<Book> {

  @Override
  public Book read(XmlReader reader) throws IOException {

    Book book = new Book();
    while (reader.hasAttribute()) {
      if (reader.nextAttributeName().equals("id")) {
        book.id = reader.nextAttributeValue();
      } else {
        reader.skipAttributeValue();
      }
    }

    while (reader.hasElement()) {
      boolean skipped = false;
      reader.beginElement();
      String elementName = reader.nextElementName();
      if (elementName.equals("author")) {
        book.author = reader.nextTextContent();
      } else if (elementName.equals("title")) {
        book.title = reader.nextTextContent();
      } else if (elementName.equals("genre")) {
        book.genre = reader.nextTextContent();
      } else if (elementName.equals("price")) {
        book.price = Double.parseDouble(reader.nextTextContent());
      } else if (elementName.equals("description")) {
        book.description = reader.nextTextContent();
      } else {
        // skip publish_date
        reader.skipRemainingElement();
        skipped = true;
      }

      if (!skipped)
        reader.endElement();
    }

    return book;


  }
}
