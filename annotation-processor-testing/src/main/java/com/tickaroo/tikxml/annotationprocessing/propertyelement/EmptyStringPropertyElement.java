package com.tickaroo.tikxml.annotationprocessing.propertyelement;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
public class EmptyStringPropertyElement {
  @PropertyElement
  String empty;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EmptyStringPropertyElement)) return false;

    EmptyStringPropertyElement that = (EmptyStringPropertyElement) o;

    return empty != null ? empty.equals(that.empty) : that.empty == null;
  }

  @Override public int hashCode() {
    return empty != null ? empty.hashCode() : 0;
  }
}
