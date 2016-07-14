package com.tickaroo.tikxml.annotationprocessing.elementlist;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.element.EmptyTag;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class EmptyTagList {

  @Element
  List<EmptyTag> tags;
}
