package com.tickaroo.tikxml.regressiontests.paths;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Person {
  @PropertyElement int id;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Person)) return false;

    Person person = (Person) o;

    return id == person.id;
  }

  @Override public int hashCode() {
    return id;
  }
}


