package com.tickaroo.tikxml.annotationprocessing.propertyelement;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
public class EmptyDoublePropertyElement {
  @PropertyElement
  double empty;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EmptyDoublePropertyElement)) return false;

    EmptyDoublePropertyElement that = (EmptyDoublePropertyElement) o;

    return Double.compare(that.empty, empty) == 0;
  }

  @Override public int hashCode() {
    long temp = Double.doubleToLongBits(empty);
    return (int) (temp ^ (temp >>> 32));
  }
}
