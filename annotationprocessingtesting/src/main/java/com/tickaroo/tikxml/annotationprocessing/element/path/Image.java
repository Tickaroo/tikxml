package com.tickaroo.tikxml.annotationprocessing.element.path;

import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Image {

  @Override public boolean equals(Object obj) {
    return obj instanceof Image;
  }
}
