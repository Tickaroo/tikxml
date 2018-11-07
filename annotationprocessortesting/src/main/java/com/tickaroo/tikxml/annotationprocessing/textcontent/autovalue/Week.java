package com.tickaroo.tikxml.annotationprocessing.textcontent.autovalue;

import com.google.auto.value.AutoValue;
import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
@Xml
@AutoValue
public abstract class Week {

  @Element
  public abstract List<Day> days();
}
