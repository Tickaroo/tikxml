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
}
