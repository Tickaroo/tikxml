package com.tickaroo.tikxml.annotationprocessing.element;

import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "empty")
public class EmptyTag {

  @Override public boolean equals(Object obj) {
    return obj instanceof EmptyTag;
  }
}
