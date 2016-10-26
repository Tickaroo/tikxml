package com.tickaroo.tikxml.regressiontests.paths;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

@Xml
public class Employee extends Person {
  @PropertyElement String name;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Employee)) return false;
    if (!super.equals(o)) return false;

    Employee employee = (Employee) o;

    return name != null ? name.equals(employee.name) : employee.name == null;
  }

  @Override public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}