package com.tickaroo.tikxml.annotationprocessing.propertyelement;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class EmptyLongPropertyElement {
  @PropertyElement
  long empty;
}
