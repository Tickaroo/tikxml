package com.tickaroo.tikxml.regressiontests.paths;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.ElementNameMatcher;
import com.tickaroo.tikxml.annotation.Path;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;
import java.util.Objects;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Company {

  @Path("department/persons")
  @Element(typesByElement = {
      @ElementNameMatcher(type = Person.class),
      @ElementNameMatcher(type = Employee.class),
      @ElementNameMatcher(type = Boss.class)
  })
  public List<Person> persons;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Company)) return false;

    Company company = (Company) o;

    return Objects.equals(persons, company.persons);
  }

  @Override public int hashCode() {
    return persons != null ? persons.hashCode() : 0;
  }
}
