package com.tickaroo.tikxml.annotationprocessing.elementlist.generic;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.Objects;

@Xml
public class Boss implements Person2 {

  @Attribute String firstName;
  @Attribute String lastName;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Boss)) return false;

    Boss boss = (Boss) o;

    if (!Objects.equals(firstName, boss.firstName)) return false;
    return Objects.equals(lastName, boss.lastName);
  }

  @Override public int hashCode() {
    int result = firstName != null ? firstName.hashCode() : 0;
    result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
    return result;
  }

}
