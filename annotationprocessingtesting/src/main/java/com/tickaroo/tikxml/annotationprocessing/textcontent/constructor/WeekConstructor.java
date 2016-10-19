package com.tickaroo.tikxml.annotationprocessing.textcontent.constructor;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "week")
public class WeekConstructor {

  private List<DayConstructor> days;

  public WeekConstructor(
      @Element List<DayConstructor> days) {
    this.days = days;
  }

  public List<DayConstructor> getDays() {
    return days;
  }
}
