package com.tickaroo.tikxml.annotationprocessing.propertyelement;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
public class EmptyLongPropertyElement {
  @PropertyElement
  long empty;

  @Override public boolean equals(Object o) {

    if (this == o) return true;
    if (!(o instanceof EmptyLongPropertyElement)) return false;

    EmptyLongPropertyElement that = (EmptyLongPropertyElement) o;

    return empty == that.empty;
  }

  @Override public int hashCode() {
    return (int) (empty ^ (empty >>> 32));
  }
}
