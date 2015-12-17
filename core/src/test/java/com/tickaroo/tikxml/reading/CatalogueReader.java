package com.tickaroo.tikxml.reading;

import com.tickaroo.tikxml.XmlReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Hannes Dorfmann
 */
public class CatalogueReader implements XmlParser<Catalogue> {

  BooksReader booksReader = new BooksReader();

  @Override
  public Catalogue read(XmlReader reader) throws IOException {

    reader.beginElement();
    if (!reader.nextElementName().equals("catalog"))
      throw new IOException("<catalogue> expected at Path " + reader.getPath());

    Catalogue catalogue = new Catalogue();
    catalogue.books = new ArrayList<>();

    while (reader.hasElement()){
      reader.beginElement();
      if (reader.nextElementName().equals("book")){
        catalogue.books.add(booksReader.read(reader));
      }
      reader.endElement();
    }

    reader.endElement();

    return catalogue;
  }
}
