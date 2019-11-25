package com.tickaroo.tikxml.annotationprocessing.textcontent;

import com.tickaroo.tikxml.annotation.TextContent;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.Objects;

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

    return Objects.equals(name, day.name);
  }

  @Override public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
