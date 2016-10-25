package com.tickaroo.tikxml.annotationprocessing.textcontent.constructor;

import com.tickaroo.tikxml.annotation.TextContent;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "day")
public class DayConstructor {

  private String name;

  public DayConstructor(@TextContent String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DayConstructor)) return false;

    DayConstructor that = (DayConstructor) o;

    return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
