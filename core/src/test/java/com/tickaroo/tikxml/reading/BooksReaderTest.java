package com.tickaroo.tikxml.reading;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.XmlReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class BooksReaderTest {


  @Test
  public void read() throws IOException {
    XmlReader reader = TestUtils.readerFromFile("books.xml");

    Catalogue catalogue = new CatalogueReader().read(reader);

    Assert.assertEquals(12, catalogue.books.size());

  }
}
