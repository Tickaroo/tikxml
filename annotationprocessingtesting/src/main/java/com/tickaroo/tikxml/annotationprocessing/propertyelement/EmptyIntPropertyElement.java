package com.tickaroo.tikxml.annotationprocessing.propertyelement;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
public class EmptyIntPropertyElement {
  @PropertyElement
  int empty;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EmptyIntPropertyElement)) return false;

    EmptyIntPropertyElement that = (EmptyIntPropertyElement) o;

    return empty == that.empty;
  }

  @Override public int hashCode() {
    return empty;
  }
}
