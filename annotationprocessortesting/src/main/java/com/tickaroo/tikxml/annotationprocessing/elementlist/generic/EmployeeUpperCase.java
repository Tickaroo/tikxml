package com.tickaroo.tikxml.annotationprocessing.elementlist.generic;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.Objects;

@Xml
public class EmployeeUpperCase implements Person {

  @PropertyElement String name;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Employee)) return false;

    Employee employee = (Employee) o;
    return Objects.equals(name, employee.name);
  }

  @Override public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
