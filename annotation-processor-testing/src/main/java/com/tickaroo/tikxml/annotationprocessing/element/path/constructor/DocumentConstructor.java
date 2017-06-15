package com.tickaroo.tikxml.annotationprocessing.element.path.constructor;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Path;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.element.path.Image;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "document")
public class DocumentConstructor {

  private Image image;

  DocumentConstructor(@Path("toSkip") @Element Image image){
    this.image = image;
  }

  public Image getImage() {
    return image;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DocumentConstructor)) return false;

    DocumentConstructor that = (DocumentConstructor) o;

    return image != null ? image.equals(that.image) : that.image == null;
  }

  @Override public int hashCode() {
    return image != null ? image.hashCode() : 0;
  }
}
