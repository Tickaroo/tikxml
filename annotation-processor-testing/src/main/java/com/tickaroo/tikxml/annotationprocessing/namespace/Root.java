package com.tickaroo.tikxml.annotationprocessing.namespace;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Root {
  @PropertyElement(name = "x:name") String name;
  @Element(name = "m:child") Child child;
}
