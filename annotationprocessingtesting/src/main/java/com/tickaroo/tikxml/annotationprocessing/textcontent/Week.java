package com.tickaroo.tikxml.annotationprocessing.textcontent;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Week {

  @Element
  List<Day> days;

}
