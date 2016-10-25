package com.tickaroo.tikxml.annotationprocessing.textcontent;

import com.tickaroo.tikxml.annotation.TextContent;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Day {

  @TextContent String name;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Day)) return false;

    Day day = (Day) o;

    return name != null ? name.equals(day.name) : day.name == null;
  }

  @Override public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
