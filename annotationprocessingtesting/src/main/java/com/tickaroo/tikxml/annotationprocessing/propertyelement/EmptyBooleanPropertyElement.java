package com.tickaroo.tikxml.annotationprocessing.propertyelement;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
public class EmptyBooleanPropertyElement {
  @PropertyElement
  boolean empty;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EmptyBooleanPropertyElement)) return false;

    EmptyBooleanPropertyElement that = (EmptyBooleanPropertyElement) o;

    return empty == that.empty;
  }

  @Override public int hashCode() {
    return (empty ? 1 : 0);
  }
}
