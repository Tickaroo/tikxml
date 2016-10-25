package com.tickaroo.tikxml.annotationprocessing.elementlist;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.element.EmptyTag;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class EmptyTagList {

  @Element
  List<EmptyTag> tags;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EmptyTagList)) return false;

    EmptyTagList that = (EmptyTagList) o;

    return tags != null ? tags.equals(that.tags) : that.tags == null;
  }

  @Override public int hashCode() {
    return tags != null ? tags.hashCode() : 0;
  }
}
