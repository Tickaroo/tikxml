package com.tickaroo.tikxml.annotationprocessing.elementlist.constructor;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.ScanMode;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.element.EmptyTag;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
@Xml(scanMode = ScanMode.ANNOTATIONS_ONLY)
public class EmptyTagListConstructor {


  private List<EmptyTag> tags;

  public EmptyTagListConstructor(
      @Element List<EmptyTag> tags) {
    this.tags = tags;
  }

  public List<EmptyTag> getTags() {
    return tags;
  }
}
