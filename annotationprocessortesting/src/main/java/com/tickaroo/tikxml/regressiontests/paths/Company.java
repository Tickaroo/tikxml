package com.tickaroo.tikxml.regressiontests.paths;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.ElementNameMatcher;
import com.tickaroo.tikxml.annotation.GenericAdapter;
import com.tickaroo.tikxml.annotation.Path;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.regressiontests.paths.element.Book;
import java.util.List;
import java.util.Objects;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Company {

  @Path("department/persons")
  @Element
  public List<Person> persons;

  @Element
  public List<Person> bosses;

  @Element
  public List<Statisch> statisches;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Company)) return false;

    Company company = (Company) o;

    return Objects.equals(persons, company.persons);
  }

  @Override public int hashCode() {
    return persons != null ? persons.hashCode() : 0;
  }

  @GenericAdapter
  interface Statisch{}

  @Xml
  static class TestStatisch implements Statisch{}
}
