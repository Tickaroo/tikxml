package com.tickaroo.tikxml.regressiontests.paths.element;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Path;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;
import java.util.Objects;

@Xml
public class BookStore {

  @Path("specialBook")
  @Element public Book book;

  @Path("otherBooks")
  @Element public List<Book> books;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BookStore bookStore = (BookStore) o;
    return Objects.equals(book, bookStore.book);
  }

  @Override public int hashCode() {
    return Objects.hash(book);
  }
}
