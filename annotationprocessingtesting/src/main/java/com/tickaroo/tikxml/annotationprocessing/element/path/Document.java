package com.tickaroo.tikxml.annotationprocessing.element.path;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Path;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Document {
  @Path("toSkip") @Element Image image;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Document)) return false;

    Document document = (Document) o;

    return image != null ? image.equals(document.image) : document.image == null;
  }

  @Override public int hashCode() {
    return image != null ? image.hashCode() : 0;
  }
}
