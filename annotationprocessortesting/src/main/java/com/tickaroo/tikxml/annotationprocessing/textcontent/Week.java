package com.tickaroo.tikxml.annotationprocessing.textcontent;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;
import java.util.Objects;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Week {

  @Element
  List<Day> days;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Week)) return false;

    Week week = (Week) o;

    return Objects.equals(days, week.days);
  }

  @Override public int hashCode() {
    return days != null ? days.hashCode() : 0;
  }
}
