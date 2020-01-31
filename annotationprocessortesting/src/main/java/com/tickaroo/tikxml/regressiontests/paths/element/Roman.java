package com.tickaroo.tikxml.regressiontests.paths.element;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.Objects;

@Xml
public class Roman implements Book {

  @Attribute public String name;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Roman roman = (Roman) o;
    return Objects.equals(name, roman.name);
  }

  @Override public int hashCode() {
    return Objects.hash(name);
  }
}
