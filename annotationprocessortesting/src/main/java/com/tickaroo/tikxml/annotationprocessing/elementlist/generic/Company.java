package com.tickaroo.tikxml.annotationprocessing.elementlist.generic;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.ElementNameMatcher;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;
import java.util.Objects;

@Xml
public class Company {

  @PropertyElement String name;
  @Element(typesByElement = {
    @ElementNameMatcher(name = "bosser", type = Boss.class)
  }) List<Person> persons;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Company)) return false;

    Company company = (Company) o;

    if (!Objects.equals(name, company.name)) return false;
    return Objects.equals(persons, company.persons);
  }

  @Override public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (persons != null ? persons.hashCode() : 0);
    return result;
  }
}
